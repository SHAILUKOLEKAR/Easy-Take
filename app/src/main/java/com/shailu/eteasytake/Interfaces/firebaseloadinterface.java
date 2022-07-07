package com.shailu.eteasytake.Interfaces;

import com.shailu.eteasytake.model.User_history_model;
import com.shailu.eteasytake.model.User_history_model;

import java.util.List;

public interface firebaseloadinterface {
    void firebaseloadfail(String messege);

    void onfirebaseloadsuccess(List<User_history_model> itemGroupList);
}
