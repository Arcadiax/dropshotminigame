package net.mysteryspace.dropshotminigame.Util;

public class Timer {

    private int _time;
    private boolean _disposed;

    public Timer(int time){
        _time = time;
    }

    public void Tick(){
        if(_time > 0)
            _time--;
    }

    public boolean IsEnded(){
        return _time <= 0;
    }

    public void Dispose(){
        _disposed = true;
    }

    public boolean IsDisposed(){
        return _disposed;
    }

    public int GetTime(){
        return _time;
    }

}
