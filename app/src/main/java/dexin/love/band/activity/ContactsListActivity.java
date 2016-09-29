package dexin.love.band.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import dexin.love.band.R;
import dexin.love.band.bean.Contacts;
import dexin.love.band.ui.PinnedSectionListView;

/**
 * Created by Administrator on 2016/9/20.导入通讯录列表
 */
public class ContactsListActivity extends Activity implements AdapterView.OnItemClickListener {
    private PinnedSectionListView listView;
    private ArrayList<Contacts> contactses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_friend);
        listView = (PinnedSectionListView) findViewById(R.id.fragment_friend_psl);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle == null)
            return;
        contactses = (ArrayList<Contacts>) bundle.getSerializable("contacts");
        System.out.print(contactses);
        listView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
