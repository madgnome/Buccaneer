(function(window) {
    var hasOwn = Object.prototype.hasOwnProperty,
        callbacks  = {},
        previousRequire = window.require,
        previousDefine = window.define,
        modules = {
            require : require,
            jquery : window.AJS ? window.AJS.$ : window.jQuery,
            underscore : window._,
            aui : window.AJS
        };

    function require(moduleName) {
        if (!hasOwn.call(modules, moduleName)) {
            evaluate(moduleName);
        }
        return modules[moduleName];
    }

    function define(moduleName, dependencies, callback) {

        if (hasOwn.call(modules, moduleName) || hasOwn.call(callbacks, moduleName)) {
            throw new Error("Module '" + moduleName + "' is already defined.");
        }

        if (!callback) {// dependencies is optional
            callback = dependencies;
            dependencies = undefined;
        }

        callbacks[moduleName] = {
            dependencies : dependencies,
            callback : callback
        };
    }

    function evaluate(moduleName) {
        var i, len, exports, dependencies, callback, c, hasExports, callbackResult;

        if (!hasOwn.call(callbacks, moduleName)) {
            throw new Error("Module '" + moduleName + "' has not yet been defined.");
        }

        exports = {};
        c = callbacks[moduleName];
        callback = c.callback;
        dependencies = c.dependencies;

        if (typeof callback === 'function') {
            if (!dependencies || !dependencies.length) {
                callbackResult = callback(require, exports);
            } else {
                dependencies = dependencies.slice(0);

                for(i = 0, len = dependencies.length; i < len; i++) {
                    if (dependencies[i] === 'exports') {
                        dependencies[i] = exports;
                        hasExports = true;
                    } else {
                        dependencies[i] = require(dependencies[i]);
                    }
                }

                callbackResult = callback.apply(null, dependencies);
            }
        } else {
            callbackResult = callback;
        }

        modules[moduleName] = (hasExports || callbackResult === undefined) ? exports : callbackResult;
    }

    window.require = require;
    window.define = define;

    function noConflict() {
        window.require = previousRequire;
        window.define = previousDefine;

        return this;
    }

    require.noConflict = noConflict;
    define.noConflict = noConflict;

    window.requireLite = require; // Used for testing
    window.defineLite = define; // Used for testing
})(window || this);
