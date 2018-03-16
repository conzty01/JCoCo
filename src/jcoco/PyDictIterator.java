/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Mar 3, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PyDictIterator extends PyPrimitiveTypeAdapter {

    private PyDict dict;
    private Iterator<HashMap.Entry<PyObject, PyObject>> it = dict.data.entrySet().iterator();

    public PyDictIterator(PyDict lst) {
        super("dictkey_iterator", PyType.PyTypeId.PyDictKeyIteratorType);
        this.dict = dict;
        initMethods(funs());

    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size() + ".");
                }

                PyDictIterator self = (PyDictIterator) args.get(args.size() - 1);

                return self;
            }
        });

        funs.put("__next__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size() + ".");
                }

                PyDictIterator self = (PyDictIterator) args.get(args.size() - 1);

                if (!self.it.hasNext()) {
                    throw new PyException(PyException.ExceptionType.PYSTOPITERATIONEXCEPTION, "stop it");
                }

                return self.it.next().getKey();
            }
        });

        return funs;
    }
}
