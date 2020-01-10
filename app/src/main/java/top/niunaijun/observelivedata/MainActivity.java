package top.niunaijun.observelivedata;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import top.niunaijun.livedata.annotations.ObserveLiveData;
import top.niunaijun.livedata.api.ObserveManager;
import top.niunaijun.observelivedata.model.BaseViewModel;
import top.niunaijun.observelivedata.model.Simulation2ViewModel;
import top.niunaijun.observelivedata.model.SimulationViewModel;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public MutableLiveData<String> mToastLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimulationViewModel viewModel = new SimulationViewModel();
        Simulation2ViewModel viewMode2 = new Simulation2ViewModel();

        // register LiveData
        ObserveManager.register(this, this, viewModel, viewMode2);

        // data changed
        viewModel.mConfig1.setValue("i am config1");
        viewMode2.mConfig2.setValue("i am config2");

        // viewModel and viewMode2 extends BaseViewModel
        viewModel.mBaseConfig.setValue("i am base config1");
        viewMode2.mBaseConfig.setValue("i am base config2");

        mToastLiveData.setValue("i am activity");
    }

    @ObserveLiveData(target = SimulationViewModel.class, field = "mConfig1")
    public void onConfig(String config) {
        Log.d(TAG, "from SimulationViewModel: " + config);
    }

    @ObserveLiveData(target = Simulation2ViewModel.class, field = "mConfig2")
    public void onConfig2(String config) {
        Log.d(TAG, "from Simulation2ViewModel: " + config);
    }

    // Support parent class
    @ObserveLiveData(target = BaseViewModel.class, field = "mBaseConfig")
    public void onBaseConfig(String config) {
        Log.d(TAG, "from BaseViewModel: " + config);
    }

    @ObserveLiveData(field = "mToastLiveData")
    public void onToast(String msg) {
        Toast.makeText(this, "receive : " + msg, Toast.LENGTH_SHORT).show();
    }
}
