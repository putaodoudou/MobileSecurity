package practice.mobilesecurity.chapter03.test;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;
import java.util.Random;

import practice.mobilesecurity.chapter03.db.dao.BlackNumberDao;
import practice.mobilesecurity.chapter03.entity.BlackContactInfo;

/**
 * 操作黑名单的测试类
 */
public class TestBlackNumberDao extends AndroidTestCase {
    private Context context;

    @Override
    protected void setUp() throws Exception {
        context = getContext();
        super.setUp();
    }

    public void testAdd() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(context);
        Random random = new Random(8979);
        for (long i = 1; i < 30; i++) {
            BlackContactInfo info = new BlackContactInfo();
            info.setPhoneNumber(13500000000L + i + "");
            info.setContactName("zhangsan" + i);
            info.setMode(random.nextInt(3) + 1);
            dao.add(info);
        }
    }


    public void testDelete() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(context);

        BlackContactInfo info = new BlackContactInfo();
        for (long i = 1; i < 5; i++) {
            info.setPhoneNumber(13500000000L + i + "");
            dao.delete(info);
        }
    }

    public void testGetPageBlackNumber() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(context);
        List<BlackContactInfo> list = dao.getPageBlackNumber(2, 5);
        for (int i = 0; i < list.size(); i++) {
            Log.i("TestBlackNumberDao", list.get(i).getPhoneNumber());
        }
    }

    public void testGetBlackContactMode() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(context);
        int mode = dao.getBlackContactMode(13500000008l + "");
        Log.i("TestBlackNumberDao", mode + "");
    }

    public void testGetTotalNumber() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(context);
        int total = dao.getTotalNumber();
        Log.i("TestBlackNumberDao", "Total Number" + total);
    }

    public void testIsNumberExist() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(context);
        boolean isExist = dao.IsNumberExist(13500000008l + "");
        if (isExist) {
            Log.i("TestBlackNumberDao", "exist");
        } else {
            Log.i("TestBlackNumberDao", "not exist");
        }
    }
}
