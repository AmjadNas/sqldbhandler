package test;

import com.amjadnas.sqldbmanager.annotations.Handler;

@Handler(entities = {User.class})
public class MyHandler {

    private Dummy dummDao;

    public Dummy getDummDao() {
        return dummDao;
    }
}
