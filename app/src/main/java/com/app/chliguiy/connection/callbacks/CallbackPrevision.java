package com.app.chliguiy.connection.callbacks;

import com.app.chliguiy.model.Privision;
import com.app.chliguiy.model.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallbackPrevision  implements Serializable{
    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public List<Privision> privision = new ArrayList<>();

}
