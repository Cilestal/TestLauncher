package ua.dp.michaellang.testlauncher;

import android.support.v4.app.Fragment;

/**
 * Date: 01.08.2017
 *
 * @author Michael Lang
 */
public class LauncherActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return LauncherFragment.newInstance();
    }
}
