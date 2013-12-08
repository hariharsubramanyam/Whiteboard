package protocol;

import java.util.List;
import java.util.Set;

import adts.Line;

public interface Client {
    public void onReceiveUsernameChanged(String rcvdName);
    public void onReceiveBoardIDs(List<Integer> rcvdIDs);
    public void onReceiveWelcome(int id);
    public void onReceiveDraw(Line l);
    public void onReceiveBoardLines(List<Line> ls, Set<String> userNames);
    public void onReceiveClear();
    public void onReceiveUsers(List<String> users);
}
