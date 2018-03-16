/**
 * PyDict.java
 * Author: Tyler Conzett (c) 2018 Created on Mar 9, 2018.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The PyDict class implements lists similarly to the way they are
 * implemented in Python. PyDicts have O(1) access time to any element
 * of the list. They are implemented using the java ArrayList class so they
 * exhibit the same characteristics as a java ArrayList. For instance,
 * appending to a PyDict has the same running time as the ArrayList add method.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

public class PyDict extends PyPrimitiveTypeAdapter {

    protected Hashtable<PyObject, PyObject> data = new Hashtable<PyObject, PyObject>();

    public PyDict() {
        super("dict", PyTypeId.PyDictType);
        initMethods(funs());
    }
    
    public void setVal(PyObject key, PyObject val) {
        
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyDictType);
    }

    public int len() {
        return this.data.size();
    }

    @Override
    public String str() {
        String str = "{";
        ArrayList<PyObject> args = new ArrayList<PyObject>();
        try {
            for (int i = 0; i < this.data.size(); i++) {
                String key = ((PyStr) this.data.get(i).callMethod("__repr__", args)).str();
                String value = ((PyObject) data.get(key)).str();
                str += ((PyStr) this.data.get(i).callMethod("__repr__", args)).str();

                if (i < data.size() - 1) {
                    str += ", ";
                }
            }
        } catch (PyException e) {
            System.err.println(e.getMessage());
            e.printTraceBack();
        }
        str += "}";

        return str;
    }
    
    @Override
    public int hashCode() {
        ArrayList<PyObject> args = new ArrayList<PyObject>();
        PyInt val = (PyInt) this.callMethod("__hash__", args);
        return val.getVal();
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__getitem__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyDict self = (PyDict) args.get(args.size() - 1);
                String key = ((PyObject) args.get(0)).str();

                return self.get(key);
            }
        });
        funs.put("__setitem__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 3) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 3 arguments, got " + args.size());
                }

                PyDict self = (PyDict) args.get(args.size() - 1);
                PyObject key = (PyObject) args.get(0);
                PyObject value = (PyObject) args.get(1);

                self.data.put(key, value);

                return new PyNone();
            }
        });
        funs.put("__len__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyDict self = (PyDict) args.get(args.size() - 1);
                return new PyInt(self.data.size());
            }
        });
        funs.put("__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyDict self = (PyDict) args.get(args.size() - 1);
                return new PyDictIterator(self);
            }
        });
        funs.put("keys", new PyCallableAdapter() {
            
        });
        funs.put("values", new PyCallableAdapter() {
            
        });
        
        return funs;
    }
}
