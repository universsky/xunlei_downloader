package org.slackwareer.xunlei;

/**
 *
 */
public abstract class XunleiThread implements Runnable {
    //线程的抽象类
    private boolean _done = false;

    public boolean before() {
        return true;
    }

    public void run() {
        if (this.before()) {
            this.runing();
            this.after();
        }
        this._done = true;
        this.print("Thread end");
    }

    public void after() {

    }

    public void runing() {

    }

    public void print(String msg) {
        System.out.println(Thread.currentThread().getName() + "\": " + msg);
    }

    public boolean isDone() {
        return this._done;
    }
}
