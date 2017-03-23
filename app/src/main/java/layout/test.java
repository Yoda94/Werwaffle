package layout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.philip.werwaffle.R;

public class test extends AppCompatActivity {
    private volatile boolean bla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test4);
        final Button button = (Button) findViewById(R.id.test_bt);
        bla = true;

        Thread idk = new Thread(new Runnable() {
            @Override
            public void run() {
                while (bla){
                    doBlas();
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                        System.out.println("Interrupted");
                    }
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Butten klick!");
                    }
                });
            }
        });
        idk.start();

        Thread idk2 = new Thread(new Runnable() {
            @Override
            public void run() {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bla = false;
                    }
                });
            }
        });
        idk2.start();

    }

    private void doBlas(){
        System.out.println("asdoasudauisd");
    }
}
