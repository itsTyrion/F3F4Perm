package de.redgames.f3nperm.reflection

import com.google.common.cache.CacheBuilder
import com.google.common.collect.ImmutableMap
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.time.Duration

object Reflections {
    // -------------
    // Classes
    // -------------
    private val primitives = ImmutableMap.of(
        "int", Integer.TYPE,
        "byte", java.lang.Byte.TYPE,
        "short", java.lang.Short.TYPE,
        "long", java.lang.Long.TYPE,
        "float", java.lang.Float.TYPE,
        "double", java.lang.Double.TYPE,
        "boolean", java.lang.Boolean.TYPE,
        "char", Character.TYPE
    )

    private val classCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofHours(12)).build<String, Class<*>>()

    @Throws(ReflectionException::class)
    fun resolve(className: String): Class<*> {
        return primitives[className] ?: classCache.getIfPresent(className) ?: try {
            Class.forName(className).also { classCache.put(className, it) }
        } catch (e: ClassNotFoundException) {
            throw ReflectionException("Class '$className' could not be resolved!", e)
        }
    }

    // -------------
    // Constructors
    // -------------
    @Throws(ReflectionException::class)
    fun make(constructorDescription: String, vararg params: Any?): Any = try {

        findConstructor(constructorDescription).newInstance(*params)
    } catch (e: ReflectiveOperationException) {
        throw ReflectionException("Could not invoke constructor $constructorDescription", e)
    }

    @Throws(ReflectionException::class)
    private fun findConstructor(constructorDescription: String) = try {
        if (constructorDescription.endsWith("()")) {
            val className = constructorDescription.substring(0, constructorDescription.length - 2)
            val clazz = resolve(className)
            clazz.getConstructor()
        } else {
            val startingBracket = constructorDescription.indexOf('(')
            val endingBracket = constructorDescription.indexOf(')')
            require(!(startingBracket == -1 || endingBracket == -1)) { "Constructor declaration must contain start and end brackets!" }

            val className = constructorDescription.substring(0, startingBracket)
            val clazz = resolve(className)
            val params = find(constructorDescription, startingBracket, endingBracket, constructorDescription)
            clazz.getConstructor(*params.toTypedArray())
        }
    } catch (e: NoSuchMethodException) {
        throw ReflectionException("Could not find constructor!", e)
    }

    // -------------
    // Methods
    // -------------
    @Throws(ReflectionException::class)
    fun call(target: Any, methodDescription: String, vararg parameters: Any?): Any {
        val clazz: Class<*> = target.javaClass
        val method = findMethod(clazz, methodDescription)
        return try {
            method.invoke(target, *parameters)
        } catch (e: InvocationTargetException) {
            throw ReflectionException("Could not call method '$methodDescription' on '${clazz.canonicalName}'!")
        } catch (e: IllegalAccessException) {
            throw ReflectionException("Could not call method '$methodDescription' on '${clazz.canonicalName}'!")
        }
    }

    @Throws(ReflectionException::class)
    private fun findMethod(clazz: Class<*>, methodDescription: String): Method {
        return try {
            if (methodDescription.endsWith("()")) {
                val methodName = methodDescription.substring(0, methodDescription.length - 2)
                return clazz.getMethod(methodName)
            }
            val startingBracket = methodDescription.indexOf('(')
            val endingBracket = methodDescription.indexOf(')')
            require(startingBracket != -1 && endingBracket != -1) { "Method declaration must contain start and end brackets!" }
            val methodName = methodDescription.substring(0, startingBracket)
            val params = find(methodDescription, startingBracket, endingBracket, methodName)
            clazz.getMethod(methodName, *params.toTypedArray())
        } catch (e: NoSuchMethodException) {
            throw ReflectionException("Could not find method!", e)
        }
    }

    @Throws(ReflectionException::class)
    private fun find(methodDesc: String, start: Int, end: Int, methodName: String): List<Class<*>> {
        val params = ArrayList<Class<*>>()
        var from = start + 1
        var to = methodName.indexOf(',')
        while (to != -1) {
            val paramName = methodDesc.substring(from, to)
            params.add(resolve(paramName))
            from = to + 1
            to = methodName.indexOf(',', to + 1)
        }
        val paramName = methodDesc.substring(from, end)
        params.add(resolve(paramName))
        return params
    }

    // -------------
    // Fields
    // -------------

    @Throws(ReflectionException::class)
    fun get(target: Any, name: String): Any {
        val clazz: Class<*> = target.javaClass
        try {
            return clazz.getField(name).get(target)
        } catch (e: NoSuchFieldException) {
            throw ReflectionException("Could not access field " + name + " on " + clazz.canonicalName, e)
        } catch (e: IllegalAccessException) {
            throw ReflectionException("Could not access field " + name + " on " + clazz.canonicalName, e)
        }
    }
    @Throws(ReflectionException::class)
    fun getPrivate(target: Any, name: String): Any {
        val clazz = target.javaClass
        return try {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            field[target]
        } catch (e: NoSuchFieldException) {
            throw ReflectionException("Could not access field " + name + " on " + clazz.canonicalName, e)
        } catch (e: IllegalAccessException) {
            throw ReflectionException("Could not access field " + name + " on " + clazz.canonicalName, e)
        }
    }

    @Throws(ReflectionException::class)
    fun setPrivate(target: Any, name: String, value: Any?) {
        val clazz = target.javaClass
        try {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            field[target] = value
        } catch (e: NoSuchFieldException) {
            throw ReflectionException("Could not write field " + name + " on " + clazz.canonicalName, e)
        } catch (e: IllegalAccessException) {
            throw ReflectionException("Could not write field " + name + " on " + clazz.canonicalName, e)
        }
    }
}