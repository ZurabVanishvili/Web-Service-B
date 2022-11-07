package ge.ufc.webservices.ws;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class ReturnJsonClass {
    int sys_id;

    public int getSys_id() {
        return sys_id;
    }

    public void setSys_id(int sys_id) {
        this.sys_id = sys_id;
    }

    @Override
    public String toString() {
        return "asf{" +
                "sys_id=" + sys_id +
                '}';
    }
}
