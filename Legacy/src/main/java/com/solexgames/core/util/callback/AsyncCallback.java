package com.solexgames.core.util.callback;

import java.io.Serializable;

public interface AsyncCallback extends Serializable {

    /**
     * A callback after running a task
     */
    <T> T callback();

}
