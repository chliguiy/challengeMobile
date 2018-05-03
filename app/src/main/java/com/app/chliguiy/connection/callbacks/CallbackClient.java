package com.app.chliguiy.connection.callbacks;

import com.app.chliguiy.model.Category;
import com.app.chliguiy.model.Client;
import com.app.chliguiy.model.NewsInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallbackClient implements Serializable {


    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public List<Client> client = new ArrayList<>();
}
