package test;

import com.amjadnas.sqldbmanager.annotations.Handler;

@Handler(entities = {Movie.class, User.class})
public class MyHandler {

    private Dummy dummDao;

    public Dummy getDummDao() {
        return dummDao;
    }
}
