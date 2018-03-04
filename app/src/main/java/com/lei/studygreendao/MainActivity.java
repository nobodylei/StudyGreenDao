package com.lei.studygreendao;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lei.entity.Father;
import com.lei.entity.Son;
import com.lei.greendao.DaoMaster;
import com.lei.greendao.DaoSession;
import com.lei.greendao.FatherDao;
import com.lei.greendao.SonDao;

import org.greenrobot.greendao.query.LazyList;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DaoMaster master;
    private DaoSession session;
    private SQLiteDatabase db;
    private SonDao sonDao;
    private FatherDao fatherDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openDb();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        //addPerson();
        //queryAll();
        //queryLike();
        //queryBetween();
        //queryLikeAsc();
        querySql();
    }

    /**
     * 打开一个数据库
     */
    private void openDb() {
        db = new DaoMaster.DevOpenHelper(MainActivity.this,
                "preson.db",
                null).getWritableDatabase();
        master = new DaoMaster(db);
        session = master.newSession();
        sonDao = session.getSonDao();
        fatherDao = session.getFatherDao();
    }

    /**
     * 插入数据
     */
    private void addPerson() {
        Son ccc = new Son();
        ccc.setName("lisi");
        ccc.setAge(10);
        Father James = new Father();
        James.setAge(45);
        James.setName("zhangsna");
        Long jamesId = fatherDao.insert(James);
        ccc.setFatherId(jamesId);
        sonDao.insert(ccc);

        /*Son abc = new Son();
        abc.setName("abc");
        abc.setAge(19);
        Father kobe = new Father();
        kobe.setAge(54);
        kobe.setName("kobe");
        Long kobeId = fatherDao.insert(kobe);
        abc.setFatherId(kobeId);
        sonDao.insert(abc);*/
    }

    /**
     * 查询所有数据
     */
    private void queryAll() {
        LazyList<Son> list = sonDao.queryBuilder().listLazy();//懒加载
        for (Son son : list) {
            Log.i("son", son.toString());
        }
        list.close();//关闭游标
        Iterator iterator = sonDao.queryBuilder().listIterator();
        while (iterator.hasNext()) {
            Son son = (Son) iterator.next();
            Log.i("son", son.toString());
        }
    }

    /**
     * 查询数据
     */
    public void queryEq() {
        Son son = sonDao.queryBuilder().
                where(SonDao.Properties.Name.eq("abc")).
                unique();
        if (son != null) {
            Log.i("son", son.toString());
        }
    }

    /**
     * 模糊查询
     */
    public void queryLike() {
        List<Son> sons = sonDao.queryBuilder().
                where(SonDao.Properties.Name.like("Tom%")).//通配符匹配
                list();
        if (sons != null) {
            for (Son son : sons) {
                Log.i("son", son.toString());
            }
        }
    }


    public void queryBetween() {
        List<Son> sons = sonDao.queryBuilder().//年龄10到20岁
                where(SonDao.Properties.Age.between(10, 30)).
                list();
        Log.i("son", sons.toString());

    }

    public void queryGt() {
        List<Son> sons = sonDao.queryBuilder().//年龄大于10岁
                where(SonDao.Properties.Age.gt(10)).
                list();
        Log.i("son", sons.toString());
        // lt < ;  ge >= ;le <= ;gt >;
    }

    public void queryLikeAsc() {//按年龄升序排列
        List data = sonDao.queryBuilder().orderAsc(SonDao.Properties.Age).list();
        Log.i("son",data.toString());
    }

    /**
     * 自定义SQL语句
     * 查询father年龄大于40的son的数据
     */
    public void querySql() {
        List data = sonDao.queryBuilder().where(
                new WhereCondition.StringCondition("FATHER_ID IN " +
                " (SELECT _ID FROM FATHER WHERE AGE > 40 )")).list();
        Log.i("son",data.toString());
    }

    public void queryThread() {
        final Query query = sonDao.queryBuilder().build();
        new Thread() {
            @Override
            public void run() {
                query.forCurrentThread();
                //得到一个安全的查询对象
                List data = query.forCurrentThread().list();
                Log.i("son",data.toString());
            }
        }.start();
    }
}
