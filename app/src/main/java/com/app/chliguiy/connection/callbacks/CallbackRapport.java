package com.app.chliguiy.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.app.chliguiy.model.Client;
import com.app.chliguiy.model.rapport;
public class CallbackRapport implements Serializable {

    public String status = "";
    public String msg = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public DataResp data = new DataResp();
    public List<rapport> rapport = new ArrayList<>();
    public static class DataResp implements Serializable {
        public Long id = -1L;
        public String code = "";
    }

}
