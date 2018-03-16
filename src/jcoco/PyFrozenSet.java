/**
 * PyFrozenSet.java
 * Author: Tyler Conzett (c) 2018 Created on Mar 09, 2018.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The PyFrozenSet class implements lists similarly to the way they are
 * implemented in Python. PyFrozenSets have O(1) access time to any element
 * of the list. They are implemented using the java ArrayList class so they
 * exhibit the same characteristics as a java ArrayList. For instance,
 * appending to a PyFrozenSet has the same running time as the ArrayList add method.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import jcoco.PyException.ExceptionType;
import static jcoco.PyObjectAdapter.selflessArgs;
import jcoco.PyType.PyTypeId;

public class PyFrozenSet extends PyPrimitiveTypeAdapter {

    protected HashSet<PyObject> data;

    public PyFrozenSet(HashSet<PyObject> data) {
        super("frozenset", PyTypeId.PyFrozenSetType);
        this.data = data;
        initMethods(funs());
    }

    public PyFrozenSet() {
        this(new HashSet<PyObject>());
    }

    public int len() {
        return this.data.size();
    }

    @Override
    public String str() {
        String str = "frozenset({";
        ArrayList<PyObject> args = new ArrayList<PyObject>();
        try {
            int i = 0;
            for (PyObject obj : data) {

                str += ((PyStr) obj.callMethod("__repr__", args)).str();

                if (i < data.size() - 1) {
                    str += ", ";
                }
                i += 1;
            }
        } catch (PyException e) {
            System.err.println(e.getMessage());
            e.printTraceBack();
        }
        str += "})";

        return str;
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                return new PyFrozenSetIterator(self);
            }
        });
        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyInt __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                return new PyInt(self.data.hashCode());
            }
        });
        funs.put("__eq__", new PyCallableAdapter() {
            @Override
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                ///// Look at List __eq__ >> compare all items of the set to see if they are the same
                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                PyFrozenSet other = (PyFrozenSet) args.get(0);
                ArrayList<PyObject> newargs = new ArrayList<PyObject>();
                
                //We should check the type of args[0] before casting it. 
                if (self.getType().typeId() != other.getType().typeId()) {
                    return new PyBool(false);

                } else if (self.data.size() != other.data.size()) {
                    return new PyBool(false);

                }

                for (PyObject item : other.data) {
                    newargs.add(item);
                    PyBool result = (PyBool) item.callMethod("__eq__", newargs);
                    if (!result.getVal()) {
                        return result;
                    }

                    newargs.remove(newargs.size() - 1); // remove the argument from the vector
                }

                return new PyBool(true);
            }
        });
        funs.put("__len__", new PyCallableAdapter() {
            @Override
            public PyInt __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                return new PyInt(self.data.size());
            }
        });
        funs.put("__contains__", new PyCallableAdapter() {
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }
                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                if (self.data.contains(args.get(0))) {
                    return new PyBool(true);
                }

                return new PyBool(false);
            }
        });
        funs.put("__notin__", new PyCallableAdapter() {
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }
                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                if (!self.data.contains(args.get(0))) {
                    return new PyBool(true);
                }

                return new PyBool(false);
            }
        });
        funs.put("isdisjoint", new PyCallableAdapter() {
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                HashSet self = ((PyFrozenSet) args.get(args.size() - 1)).data;
                HashSet other = ((PyFrozenSet) args.get(0)).data;

                for (Object item : self) {
                    if (other.contains(item)) {
                        return new PyBool(false);
                    }
                }
                return new PyBool(true);
            }
        });
        funs.put("__le__", new PyCallableAdapter() {
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                PyFrozenSet other = (PyFrozenSet) args.get(0);

                for (PyObject item : self.data) {
                    if (!other.data.contains(item)) {
                        return new PyBool(false);
                    }
                }
                return new PyBool(true);
            }
        });
        funs.put("issubset", new PyCallableAdapter() {
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                return (PyBool) self.callMethod("__le__", selflessArgs(args));
            }
        });
        funs.put("__lt__", new PyCallableAdapter() {
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }
                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);

                if (((PyBool) self.callMethod("__le__", selflessArgs(args))).getVal()
                        && !((PyBool) self.callMethod("__eq__", selflessArgs(args))).getVal()) {
                    return new PyBool(true);
                }

                return new PyBool(false);
            }
        });
        funs.put("__ge__", new PyCallableAdapter() {
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                PyFrozenSet other = (PyFrozenSet) args.get(0);

                for (PyObject item : other.data) {
                    if (!self.data.contains(item)) {
                        return new PyBool(false);
                    }
                }
                return new PyBool(true);
            }
        });
        funs.put("issuperset", new PyCallableAdapter() {
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                return (PyBool) self.callMethod("__ge__", selflessArgs(args));
            }
        });
        funs.put("__gt__", new PyCallableAdapter() {
            public PyBool __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                PyFrozenSet other = (PyFrozenSet) args.get(0);

                if (((PyBool) self.callMethod("__ge__", selflessArgs(args))).getVal()
                        && !((PyBool) self.callMethod("__eq__", selflessArgs(args))).getVal()) {
                    return new PyBool(true);
                }

                return new PyBool(false);
            }
        });
        funs.put("__or__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() < 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected at least 1 argument, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                int i;
                HashSet<PyObject> result = (HashSet<PyObject>) self.data.clone();

                for (i = 0; i < args.size() - 1; i++) {

                    result.addAll(((PyFrozenSet) args.get(i)).data);

                }
                return new PyFrozenSet(result);
            }
        });
        funs.put("union", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() < 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected at least 1 argument, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);

                return self.callMethod("__or__", selflessArgs(args));
            }
        });
        funs.put("__and__", new PyCallableAdapter() {
            @Override
            public PyFrozenSet __call__(ArrayList<PyObject> args) {
                if (args.size() < 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected at least 1 argument, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                PyFrozenSet other = (PyFrozenSet) args.get(0);
                HashSet<PyObject> ret = new HashSet();

                for (PyObject item : other.data) {
                    if (self.data.contains(item)) {
                        ret.add(item);
                    }
                }

                return new PyFrozenSet(ret);
            }
        });
        funs.put("intersection", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() < 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected at least 1 argument, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);

                return self.callMethod("__and__", selflessArgs(args));
            }
        });
        funs.put("__sub__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() < 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected at least 1 argument, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                PyFrozenSet other = (PyFrozenSet) args.get(0);
                HashSet n = new HashSet();

                for (PyObject item : self.data) {
                    if (!other.data.contains(item)) {
                        n.add(item);
                    }
                }

                return new PyFrozenSet(n);
            }
        });
        funs.put("difference", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() < 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected at least 1 argument, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);

                return self.callMethod("__sub__", selflessArgs(args));
            }
        });
        funs.put("__pow__", new PyCallableAdapter() {
            @Override
            public PyFrozenSet __call__(ArrayList<PyObject> args) {
                if (args.size() < 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected at least 1 argument, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                PyFrozenSet other = (PyFrozenSet) args.get(0);
                HashSet<PyObject> res = new HashSet();

                for (PyObject item : self.data) {
                    if (!other.data.contains(item)) {
                        res.add(item);
                    }
                }
                for (PyObject item : other.data) {
                    if (!self.data.contains(item)) {
                        res.add(item);
                    }
                }

                return new PyFrozenSet(res);
            }
        });
        funs.put("symmetric_difference", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() < 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected at least 1 argument, got " + args.size());
                }

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);

                return self.callMethod("__pow__", selflessArgs(args));
            }
        });
        funs.put("copy", new PyCallableAdapter() {
            @Override
            public PyFrozenSet __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }
                // Return a new set with a shallow copy of s

                PyFrozenSet self = (PyFrozenSet) args.get(args.size() - 1);
                HashSet<PyObject> result = (HashSet<PyObject>) self.data.clone();
                return new PyFrozenSet(result);
            }
        });
        return funs;
    }
}
