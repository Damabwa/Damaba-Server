package com.damaba.damaba.config

import com.damaba.damaba.adapter.outbound.common.TransactionalLockManager
import com.damaba.damaba.domain.common.TransactionalLock
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

/**
 * <code>@TransactionalLock</code> annotation이 붙은 method에 transaction & lock을 적용하는 클래스
 */
@Aspect
@Component
class TransactionalLockAspect(private val transactionalLockManager: TransactionalLockManager) {
    /**
     * Transaction & Lock을 적용한 뒤 메서드를 실행한다.
     *
     * @param joinPoint annotation이 붙은 메서드 정보
     * @param transactionalLock 적용할 lock에 대한 정보가 담긴 annotation
     * @return 실행된 method의 return value
     * @throws IllegalArgumentException 설정된 parameter나 field를 찾을 수 없는 경우
     */
    @Around("@annotation(transactionalLock)")
    fun applyTransactionalLock(joinPoint: ProceedingJoinPoint, transactionalLock: TransactionalLock): Any {
        val methodSignature = joinPoint.signature as MethodSignature
        val paramNames = methodSignature.parameterNames

        val idFieldPaths = transactionalLock.idFieldName.split(".")
        val paramName = idFieldPaths[0]
        val fieldNames = idFieldPaths.drop(1)

        val paramIdx = paramNames.indexOf(paramName)
        if (paramIdx == -1) {
            throw IllegalArgumentException("Parameter '$paramName' does not exist in method '${methodSignature.name}'")
        }
        val parameterValue = joinPoint.args[paramIdx]

        val idValue = resolveNestedFieldValue(parameterValue, fieldNames)
            ?: throw IllegalArgumentException("Unable to resolve field path '${transactionalLock.idFieldName}'")

        return transactionalLockManager.executeWithLock(
            joinPoint = joinPoint,
            lockType = transactionalLock.lockType,
            domainType = transactionalLock.domainType,
            id = idValue,
        )
    }

    /**
     * `fieldNames`에 담긴 필드명을 사용하여 `obj`를 순차적으로 참조한 후, 참조 결과를 반환한다.
     *
     * @param obj 탐색의 기준이 될 객체
     * @param fieldNames 탐색할 필드 경로
     * @return 경로를 따라 탐색한 결과 값. 없거나 중간에 `null`이 있으면 `null` 반환
     */
    private fun resolveNestedFieldValue(obj: Any?, fieldNames: List<String>): Any? {
        var curObj = obj
        for (fieldName in fieldNames) {
            if (curObj == null) return null
            curObj = getFieldValue(curObj, fieldName)
        }
        return curObj
    }

    /**
     * Reflection을 사용해서 `obj`의 `fieldName`에 해당하는 필드 값을 가져온다.
     *
     * @param obj 필드 값을 가져올 대상 객체
     * @param fieldName 가져올 필드 이름
     * @return 조회된 필드 값
     * @throws IllegalArgumentException 필드 추출에 실패하거나 필드가 없을 때 발생
     */
    private fun getFieldValue(obj: Any, fieldName: String): Any? = try {
        val field = obj::class.java.declaredFields.firstOrNull { it.name == fieldName }
            ?: throw NoSuchFieldException("Field '$fieldName' does not exist in ${obj::class.java.simpleName}")
        field.isAccessible = true
        field.get(obj)
    } catch (e: Exception) {
        throw IllegalArgumentException("Failed to extract field '$fieldName' from ${obj::class.java.simpleName}", e)
    }
}
