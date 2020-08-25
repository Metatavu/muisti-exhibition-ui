package fi.metatavu.muisti.exhibitionui.script

import org.mozilla.javascript.Context
import org.mozilla.javascript.Function

/**
 * Controller class for scripting functions
 */
class ScriptController {

    companion object {

        /**
         * Executes a function from inline script function and returns returned Javascript value
         *
         * @param script script
         * @param functionName name of the function to be executed
         * @param params function parameters
         * @return returned Javascript value
         */
        fun executeInlineFunction(script: String, functionName: String, params: Array<Any>): Any? {
            val rhino = Context.enter()

            rhino.optimizationLevel = -1
            try {
                val scope = rhino.initStandardObjects()
                rhino.evaluateString(scope, script, "inline", 1, null)

                val obj = scope.get(functionName, scope)
                if (obj is Function) {
                    return obj.call(
                        rhino,
                        scope,
                        scope,
                        params.map { Context.javaToJS(it, scope) }.toTypedArray()
                    )
                    // return Context.toString(jsResult)
                }
            } finally {
                Context.exit()
            }

            return null
        }

    }
}