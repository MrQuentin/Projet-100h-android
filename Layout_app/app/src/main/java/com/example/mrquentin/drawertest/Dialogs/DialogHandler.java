package com.example.mrquentin.drawertest.Dialogs;

import android.support.v4.app.FragmentManager;

import com.example.mrquentin.drawertest.Dialogs.Fragments.BasicTextDialogFragment;

/**
 * Created by MrQuentin on 08/02/2018.
 */

public class DialogHandler {

    public void showBasicTextDialog(String title, String content, FragmentManager fm ){
        BasicTextDialogFragment editNameDialogFragment = BasicTextDialogFragment.newInstance(title, content);
        editNameDialogFragment.show(fm, "fragment_sample_dialog");
    }

}
