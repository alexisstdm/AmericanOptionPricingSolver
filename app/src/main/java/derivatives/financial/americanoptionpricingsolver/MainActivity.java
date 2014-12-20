package derivatives.financial.americanoptionpricingsolver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.TextView;

// import com.google.android.gms.ads.*;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;

import american.options.*;
import mathematical.solver.*;


public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText spotLevel;
    EditText strike;
    EditText rate;
    EditText dividend;
    EditText maturity;
    EditText volatility;
    EditText price;
    AlertDialog Error;
    Spinner european_type_spinner;
    String selected_type;
    HashMap<String,Double> parameters;
    String independentParameter;
    ProgressDialog progressDialog;
    Function myFunction;
    Double result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get a Tracker (should auto-report)
        Tracker t = ((AnalyticsHelper) getApplication()).getTracker(AnalyticsHelper.TrackerName.APP_TRACKER);
        t.setScreenName("Pantalla Principal");

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        // Buscar AdView como recurso y cargar una solicitud.
        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setFocusableInTouchMode(true);
        adView.requestFocus();

        spotLevel = (EditText) findViewById(R.id.spot_level);
        strike = (EditText) findViewById(R.id.strike);
        rate = (EditText) findViewById(R.id.rate);
        dividend = (EditText) findViewById(R.id.dividend);
        maturity = (EditText) findViewById(R.id.maturity);
        volatility = (EditText) findViewById(R.id.volatility);
        european_type_spinner = (Spinner) findViewById(R.id.type);
        price = (EditText) findViewById(R.id.price);
        result = new Double(0.0);


        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
        DecimalFormat myFormatter = new DecimalFormat("###,###,###,###,###.########", otherSymbols);

        spotLevel.setText(myFormatter.format(Double.parseDouble(spotLevel.getText().toString())));
        strike.setText(myFormatter.format(Double.parseDouble(strike.getText().toString())));
        rate.setText(myFormatter.format(Double.parseDouble(rate.getText().toString())));
        dividend.setText(myFormatter.format(Double.parseDouble(dividend.getText().toString())));
        maturity.setText(myFormatter.format(Double.parseDouble(maturity.getText().toString())));
        volatility.setText(myFormatter.format(Double.parseDouble(volatility.getText().toString())));
        price.setText(new String(""));


        ArrayAdapter<CharSequence> european_type_adapter = ArrayAdapter.createFromResource(this, R.array.american_types,
                android.R.layout.simple_spinner_item);
        european_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        european_type_spinner.setAdapter(european_type_adapter);

        View __solve = findViewById(R.id.solveButton);
        __solve.setOnClickListener(this);


        european_type_spinner.setOnItemSelectedListener(this);

        AlertDialog.Builder builderOk = new AlertDialog.Builder(this);
        builderOk.setMessage("Data Type Error")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Error = builderOk.create();

        parameters = new HashMap<String,Double>();
        independentParameter = new String("PRICE");

        this.selected_type = new String("Standard Call");
        this.calculate();
    }

    private Boolean validate(String _spotLevel, String _strike, String _rate, String _dividend, String _maturity,
                             String _volatility, String _type, String _price){

        int fieldNumber = 0;
        String textError;

        try {

            int blanks_number = 0;
            for (int i=1; i<=7; i++){
                switch (i){
                    case 1:
                        if (_spotLevel.equals("")) blanks_number++;
                        break;
                    case 2:
                        if (_strike.equals("")) blanks_number++;
                        break;
                    case 3:
                        if (_rate.equals("")) blanks_number++;
                        break;
                    case 4:
                        if (_dividend.equals("")) blanks_number++;
                        break;
                    case 5:
                        if (_maturity.equals("")) blanks_number++;
                        break;
                    case 6:
                        if (_volatility.equals("")) blanks_number++;
                        break;
                    case 7:
                        if (_price.equals("")) blanks_number++;
                        break;
                    default:
                        break;
                }
            }

            if (blanks_number > 1) {
                textError = "Only one parameter could be empty";
                Error.setMessage(textError);
                Error.show();
                return true;
            }

            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
            DecimalFormat myFormatter = new DecimalFormat("###,###,###,###,###.########", otherSymbols);

            fieldNumber = 1;
            if (!_spotLevel.equals("")){
                double spotLevel = myFormatter.parse(_spotLevel).doubleValue();}
            fieldNumber = 2;
            if (!_strike.equals("")){
                double strike = myFormatter.parse(_strike).doubleValue();}
            fieldNumber = 3;
            if (!_rate.equals("")){
                double rate = myFormatter.parse(_rate).doubleValue();}
            fieldNumber = 4;
            if (!_dividend.equals("")){
                double dividend = myFormatter.parse(_dividend).doubleValue();}
            fieldNumber = 5;
            if (!_maturity.equals("")){
                double maturity = myFormatter.parse(_maturity).doubleValue();}
            fieldNumber = 6;
            if (!_volatility.equals("")){
                double volatility = myFormatter.parse(_volatility).doubleValue();}
            fieldNumber = 7;
            if (!_price.equals("")){
                double price = myFormatter.parse(_price).doubleValue();}
        } catch(Exception e){
            switch (fieldNumber) {
                case 1:
                    textError = "Spot Level must be a real number";
                    break;
                case 2:
                    textError = "Strike be a real number";
                    break;
                case 3:
                    textError = "Rate must be a real number";
                    break;
                case 4:
                    textError = "Dividend must be an integer number";
                    break;
                case 5:
                    textError = "Maturity must be an integer number";
                    break;
                case 6:
                    textError = "Volatility must be an integer number";
                    break;
                default:
                    textError = "Fatal error";
                    break;
            }
            Error.setMessage(textError);
            Error.show();
            return true;
        }

        if (!_type.equals("Standard Put") && !_type.equals("Standard Call")
                && !_type.equals("Binary Put") && !_type.equals("Binary Call")) {
            textError = "The type is not supported";
            Error.setMessage(textError);
            Error.show();
            return true;
        }
        return false;
    }

    void parseData(){

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
        DecimalFormat myFormatter = new DecimalFormat("###,###,###,###,###.########", otherSymbols);

        try {
            if (!this.spotLevel.getText().toString().equals("")) {
                this.parameters.put("SPOT_LEVEL", myFormatter.parse(this.spotLevel.getText().toString()).doubleValue());
            } else {
                this.parameters.put("SPOT_LEVEL", 1.0);
                this.independentParameter = "SPOT_LEVEL";
            }

            if (!this.strike.getText().toString().equals("")) {
                this.parameters.put("STRIKE", myFormatter.parse(this.strike.getText().toString()).doubleValue());
            } else {
                this.parameters.put("STRIKE", 1.0);
                this.independentParameter = "STRIKE";
            }

            if (!this.rate.getText().toString().equals("")) {
                this.parameters.put("RATE", myFormatter.parse(this.rate.getText().toString()).doubleValue());
            } else {
                this.parameters.put("RATE", 0.05);
                this.independentParameter = "RATE";
            }

            if (!this.dividend.getText().toString().equals("")) {
                this.parameters.put("DIVIDEND", myFormatter.parse(this.dividend.getText().toString()).doubleValue());
            } else {
                this.parameters.put("DIVIDEND", 0.05);
                this.independentParameter = "DIVIDEND";
            }

            if (!this.maturity.getText().toString().equals("")) {
                this.parameters.put("MATURITY", myFormatter.parse(this.maturity.getText().toString()).doubleValue());
            } else {
                this.parameters.put("MATURITY", 1.0);
                this.independentParameter = "MATURITY";
            }

            if (!this.volatility.getText().toString().equals("")) {
                this.parameters.put("VOLATILITY", myFormatter.parse(this.volatility.getText().toString()).doubleValue());
            } else {
                this.parameters.put("VOLATILITY", 0.30);
                this.independentParameter = "VOLATILITY";
            }

            if (!this.price.getText().toString().equals("")) {
                this.parameters.put("PRICE", myFormatter.parse(this.price.getText().toString()).doubleValue());
            } else {
                this.parameters.put("PRICE", 0.015);
                this.independentParameter = "PRICE";
            }
        } catch (Exception e){}
    }

    public void parseOutput(Double result) {

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
        DecimalFormat myFormatter = new DecimalFormat("###,###,###,###,###.########", otherSymbols);

        if (this.independentParameter.equals("SPOT_LEVEL")) {
            this.spotLevel.setText(myFormatter.format(result));
            return;
        }
        if (this.independentParameter.equals("STRIKE")) {
            this.strike.setText(myFormatter.format(result));
            return;
        }
        if (this.independentParameter.equals("VOLATILITY")) {
            this.volatility.setText(myFormatter.format(result));
            return;
        }
        if (this.independentParameter.equals("RATE")) {
            this.rate.setText(myFormatter.format(result));
            return;
        }
        if (this.independentParameter.equals("DIVIDEND")) {
            this.dividend.setText(myFormatter.format(result));
            return;
        }
        if (this.independentParameter.equals("MATURITY")) {
            this.maturity.setText(myFormatter.format(result));
            return;
        }
        if (this.independentParameter.equals("PRICE")) {
            this.price.setText(myFormatter.format(result));
            return;
        }

    }


    private void calculate(View view){
        if (!this.validate(this.spotLevel.getText().toString(), this.strike.getText().toString(),
                this.rate.getText().toString(), this.dividend.getText().toString(),
                this.maturity.getText().toString(), this.volatility.getText().toString(),
                this.selected_type, this.price.getText().toString())) {

            this.parseData();

            if ((view==null) || (view.getId()==R.id.solveButton)){

                if (this.selected_type.equals("Standard Call"))
                    myFunction = new AmericanCallSolverFunction();
                else if (this.selected_type.equals("Standard Put"))
                    myFunction = new AmericanPutSolverFunction();
                else if (this.selected_type.equals("Binary Call"))
                    myFunction = new AmericanPutSolverFunction();
                else if (this.selected_type.equals("Binary Put"))
                    myFunction = new AmericanPutSolverFunction();
                else
                    myFunction = new AmericanCallSolverFunction();

                (new AsyncTask<String, Void, Void>() {
                    private Exception exception;

                    protected Void doInBackground(String... url) {
                        try {
                            Solver mySolver = new Solver(myFunction, new NewtonAlgorithm(), MainActivity.this.independentParameter, 0.0,
                                    MainActivity.this.parameters, 100);
                            MainActivity.this.result = mySolver.solve();
                        } catch (Exception e) {
                            this.exception = e;
                            return null;
                        }
                        return null;
                    }

                    protected void onPostExecute(Void nothing) {
                        MainActivity.this.parseOutput(MainActivity.this.result);
                        progressDialog.dismiss();
                    }
                }).execute();

                progressDialog = ProgressDialog.show(MainActivity.this, "", "Calculating...");

            }
        }

    }

    private void calculate(){
        this.calculate(null);
    }

    @Override
    public void onClick(View view) {
        this.calculate(view);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        this.selected_type = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView parent){

    }


    @Override
    public void onStart(){
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop(){
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }
}

