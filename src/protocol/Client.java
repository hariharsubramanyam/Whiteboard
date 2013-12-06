package protocol;

import java.util.List;

import adts.Line;

public interface Client {
    public void onReceiveUsernameChanged(String rcvdName);
    public void onReceiveBoardIDs(List<Integer> rcvdIDs);
    public void onReceiveWelcome(int id);
    public void onReceiveDraw(Line l);
}
