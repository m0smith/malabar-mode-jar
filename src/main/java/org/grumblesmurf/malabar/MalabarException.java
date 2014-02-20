package org.grumblesmurf.malabar;

public class MalabarException extends java.lang.Exception
{
    protected  MalabarException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public  MalabarException(Throwable arg0) {
        super(arg0);
    }

    public  MalabarException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public  MalabarException(String arg0) {
        super(arg0);
    }

    public  MalabarException() {
        super();
    }


}
