package jiaziang8.com.dpparticlesystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.jiaziang8.dplib.DPParticleSystem;

import jiaziang8.com.dpparticlesystem.utils.ScreenUtils;

public class ParticleActivity extends AppCompatActivity {
    RelativeLayout particleRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particle);
        particleRootView = (RelativeLayout)findViewById(R.id.root_view);
        DPParticleSystem particleSystem = new DPParticleSystem(this, R.mipmap.particle_sakura, particleRootView, "level_map_sakura.xml");
        particleSystem.setEmitSourcePos(ScreenUtils.getScreenWidth(this)/2, ScreenUtils.getScreenHeight(this)/5);
        particleSystem.startEmitInfinite();
    }

}
