package com.puppyland.mongnang;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.puppyland.mongnang.DTO.FontDTO;
import com.puppyland.mongnang.DTO.FunctionCountDTO;
import com.puppyland.mongnang.contract.DiaryContract;
import com.puppyland.mongnang.contract.FunctionCountContract;
import com.puppyland.mongnang.presenter.DiaryPresenter;
import com.puppyland.mongnang.presenter.FunctionCountPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.widget.FButton;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, FunctionCountContract.View, DiaryContract.view {

    public static StoreActivity mcontext;
    private String id;
    private TextView shopPoint, TX_PDF1, TX_PDF2, TX_PDF3, donate;
    private LinearLayout LL_small, LL_middle, LL_big, LL_nanum, calligraphy;;

    FunctionCountContract.Presenter fpresenter;
    DiaryContract.presenter dpresenter;

    //동영상 보상 광고
    private RewardedAd rewardedAd;
    private FButton rewardBtn;
    private BillingProcessor bp;
    String GooglePlayLicenseKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq0TP9w2X/iiW1QSlT6FmB22oaVGCUnR1ofITBRFVt3OViQOhGAVGGH46YU2eZCd2hkIG0rtyFFitdtMScS7w5eVqCkDIXAmlkTu7G6mRfZAQLDcsoQoWxfta0NalZQyGiT5QKO3qBtVX0BW+9Wc/GZq7cuSwd5x4Upuurg0aJvi+UARds2FI4JSiYQHyyw4OeXyN68QIhTaU+da7DOjc9KpK6f1tFpbLxrd/iAdxOGFYEg0vyvPKd3dnEWv3GS9NkFOudpnVrEfmWSUn55ebmhIZ/SDbIbqBe34h8493MKWfEXOBRSL4qPDDY2Jt0cfgy4tWs6+YXgdT8q4o3yx8CQIDAQAB";

    //결제 시 남은 사료량과 결제 금액 비교
    private int compare, nanumfont, pdf1, pdf2, pdf3, chat1, chat2, chat3;

    //당겨서 새로고침
    private SwipeRefreshLayout swipe_refresh_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        shopPoint = findViewById(R.id.shopPoint);

        //Config.GooglePlayLicenseKey에서는 이전에 구한 구글플레이 라이센스키를 넣으면 된다.
        bp = new BillingProcessor(this, GooglePlayLicenseKey, this);
        bp.initialize();

        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");

        fpresenter = new FunctionCountPresenter(this);
        dpresenter = new DiaryPresenter(this);

        initButton();

        //getMyPoint(id); // 초기 포인트 값 가져옴
        rewardedAd = new RewardedAd(this, "ca-app-pub-1267905668913700/6891808884");

        //당겨서 새로고침
        swipe_refresh_layout = findViewById(R.id.swipe_layout);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyPoint(id);
            }
        });

        //PDF 변환 로티
        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.Lottie_pdf);
        animationView.setAnimation("24053-pdf-symbol.json");
        animationView.playAnimation();
        animationView.loop(true);

    }

    public void initButton() {
        LL_small = findViewById(R.id.LL_small);
        LL_middle = findViewById(R.id.LL_middle);
        LL_big = findViewById(R.id.LL_big);
        LL_nanum = findViewById(R.id.LL_nanum);
        rewardBtn = findViewById(R.id.rewardBtn);
        rewardBtn.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        calligraphy = findViewById(R.id.calligraphy);

        /********************************************************************************************************************************************
         * 간식 100개
         ******************************************************************************************************************************************* */
        LL_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 실동작할때는 testBasic 처럼 puchase로 하고 그게 구입이 됐으면
                //onProductPurchased 여기서 상품 id 확인하고 거기서 실동작해야함. 지금은 그냥 테스트

                AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.purchaseproduct, null);
                builder.setView(view);

                final TextView snack = (TextView) view.findViewById(R.id.Tx_snack);
                final TextView Tx_use = (TextView) view.findViewById(R.id.Tx_use);
                final TextView Tx_cancel = (TextView) view.findViewById(R.id.Tx_cancel);
                final AlertDialog dialog = builder.create();
                getMyPoint2(snack);

                Tx_use.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bp.purchase(StoreActivity.this , "point100");
                        dialog.dismiss();
                    }
                });

                Tx_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        /********************************************************************************************************************************************
         * 간식 200개
         ******************************************************************************************************************************************* */
        LL_middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.purchaseproduct, null);
                builder.setView(view);

                final TextView snack = (TextView) view.findViewById(R.id.Tx_snack);
                final TextView Tx_use = (TextView) view.findViewById(R.id.Tx_use);
                final TextView Tx_cancel = (TextView) view.findViewById(R.id.Tx_cancel);
                final AlertDialog dialog = builder.create();
                getMyPoint2(snack);

                Tx_use.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bp.purchase(StoreActivity.this , "point200");
                        dialog.dismiss();
                    }
                });

                Tx_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        /********************************************************************************************************************************************
         * 간식 300개
         ******************************************************************************************************************************************* */
        LL_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.purchaseproduct, null);
                builder.setView(view);

                final TextView snack = (TextView) view.findViewById(R.id.Tx_snack);
                final TextView Tx_use = (TextView) view.findViewById(R.id.Tx_use);
                final TextView Tx_cancel = (TextView) view.findViewById(R.id.Tx_cancel);
                final AlertDialog dialog = builder.create();
                getMyPoint2(snack);

                Tx_use.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bp.purchase(StoreActivity.this , "point300");
                        dialog.dismiss();
                    }
                });

                Tx_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        /********************************************************************************************************************************************
         * 복고풍 폰트 구매
         ******************************************************************************************************************************************* */
        LL_nanum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String font1 = "dossaemmul.ttf";
                String font2 = "gmarketsansttflight.ttf";

                CheckPurchasedfont(font1, font2);
            }
        });
        /********************************************************************************************************************************************
         * 손글씨체 구매
         ******************************************************************************************************************************************* */
        calligraphy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String font1 = "godomaum.ttf";
                String font2 = "yanulljaregular.ttf";

                CheckPurchasedfont(font1, font2);
            }
        });




        /********************************************************************************************************************************************
         * PDF 1개 결제
         ******************************************************************************************************************************************* */
        TX_PDF1 = findViewById(R.id.TX_PDF1);
        TX_PDF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdf1 = 5;
                AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.purchaseproduct, null);
                builder.setView(view);

                final TextView snack = (TextView) view.findViewById(R.id.Tx_snack);
                final TextView Tx_use = (TextView) view.findViewById(R.id.Tx_use);
                final TextView Tx_cancel = (TextView) view.findViewById(R.id.Tx_cancel);
                final AlertDialog dialog = builder.create();
                getMyPoint2(snack);

                //구매
                Tx_use.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (compare < pdf1) {
                            Toast.makeText(getApplicationContext(), "간식을 구매해 주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            FunctionCountDTO dto = new FunctionCountDTO();
                            dto.setUserid(id);

                            //포인트
                            fpresenter.functionCount_update_point(dto, 5);
                            //상품 구매
                            fpresenter.functionCount_purchase_pdf(dto, 0);

                            Toast.makeText(StoreActivity.this, "PDF 1개 구매 완료", Toast.LENGTH_SHORT).show();
                            getMyPoint(id); // 구입하고 나면 재호출
                            dialog.dismiss();
                        }
                    }
                });

                Tx_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        /********************************************************************************************************************************************
         * PDF 10개 결제
         ******************************************************************************************************************************************* */
        TX_PDF2 = findViewById(R.id.TX_PDF2);
        TX_PDF2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdf2 = 50;

                AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.purchaseproduct, null);
                builder.setView(view);

                final TextView snack = (TextView) view.findViewById(R.id.Tx_snack);
                final TextView Tx_use = (TextView) view.findViewById(R.id.Tx_use);
                final TextView Tx_cancel = (TextView) view.findViewById(R.id.Tx_cancel);
                final AlertDialog dialog = builder.create();
                getMyPoint2(snack);

                //구매
                Tx_use.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (compare < pdf2) {
                            Toast.makeText(getApplicationContext(), "간식을 구매해 주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            FunctionCountDTO dto = new FunctionCountDTO();
                            dto.setUserid(id);

                            //포인트
                            fpresenter.functionCount_update_point(dto, 6);
                            //상품 구매
                            fpresenter.functionCount_purchase_pdf(dto, 1);

                            Toast.makeText(StoreActivity.this, "PDF 10개 구매 완료", Toast.LENGTH_SHORT).show();
                            getMyPoint(id); // 구입하고 나면 재호출
                            dialog.dismiss();
                        }
                    }
                });

                Tx_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        /********************************************************************************************************************************************
         * PDF 30개 결제
         ******************************************************************************************************************************************* */
        TX_PDF3 = findViewById(R.id.TX_PDF3);
        TX_PDF3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdf3 = 150;

                AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.purchaseproduct, null);
                builder.setView(view);

                final TextView snack = (TextView) view.findViewById(R.id.Tx_snack);
                final TextView Tx_use = (TextView) view.findViewById(R.id.Tx_use);
                final TextView Tx_cancel = (TextView) view.findViewById(R.id.Tx_cancel);
                final AlertDialog dialog = builder.create();
                getMyPoint2(snack);

                //구매
                Tx_use.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (compare < pdf3) {
                            Toast.makeText(getApplicationContext(), "간식을 구매해 주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            FunctionCountDTO dto = new FunctionCountDTO();
                            dto.setUserid(id);

                            //포인트
                            fpresenter.functionCount_update_point(dto, 7);
                            //상품 구매
                            fpresenter.functionCount_purchase_pdf(dto, 2);

                            Toast.makeText(StoreActivity.this, "PDF 30개 구매 완료", Toast.LENGTH_SHORT).show();
                            getMyPoint(id); // 구입하고 나면 재호출
                            dialog.dismiss();
                        }
                    }
                });

                Tx_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        rewardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                    @Override
                    public void onRewardedAdLoaded() { // 광고 로드가 완료되면 실행
                        // Ad successfully loaded.
                        if (rewardedAd.isLoaded()) {
                            Activity activityContext = StoreActivity.this;
                            RewardedAdCallback adCallback = new RewardedAdCallback() {
                                @Override
                                public void onRewardedAdOpened() {
                                    // Ad opened.
                                }

                                @Override
                                public void onRewardedAdClosed() {
                                    rewardedAd = createAndLoadRewardedAd();

                                }

                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem reward) {
                                    // User earned reward.

                                    FunctionCountDTO dto = new FunctionCountDTO();
                                    dto.setUserid(id);

                                    fpresenter.functionCount_update_point(dto, 0); //광고보상 사료

                                    Toast.makeText(getApplicationContext(), "1간식 적립:)", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onRewardedAdFailedToShow(AdError adError) {
                                    // Ad failed to display.
                                }
                            };
                            rewardedAd.show(activityContext, adCallback);
                        } else {
                            Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                        }
                    }

                    @Override
                    public void onRewardedAdFailedToLoad(LoadAdError adError) {
                        // Ad failed to load.
                    }
                };
                rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
            }
        });

        donate = findViewById(R.id.donate);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(StoreActivity.this , "donation");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyPoint(id); // 구입하고 나면 재호출
    }

    //내 사료가 지금 얼마 있는지 가져오는 함수
    public void getMyPoint(String userid) {
        FunctionCountDTO dto = new FunctionCountDTO();
        dto.setUserid(userid);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> getMyPoint = NetRetrofit.getInstance().getService().getMyPoint(objJson);
        getMyPoint.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    if(result==null){
                        result = "0";
                        shopPoint.setText("간식 : " + result); // 불러와서 사료 textview에 새로갱신
                        compare = Integer.parseInt(result);
                    }else{
                        shopPoint.setText("간식 : " + result); // 불러와서 사료 textview에 새로갱신
                        compare = Integer.parseInt(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
        swipe_refresh_layout.setRefreshing(false);
    }

    //남은 간식 수(다이얼로그창에서 띄우는거 전용)
    private void getMyPoint2(TextView textView) {
        FunctionCountDTO dto = new FunctionCountDTO();
        dto.setUserid(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> getMyPoint = NetRetrofit.getInstance().getService().getMyPoint(objJson);
        getMyPoint.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    textView.setText(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }

    //새로고침 시 내 간식량 확인
    public void getMyPoint3(String userid) {
        FunctionCountDTO dto = new FunctionCountDTO();
        dto.setUserid(userid);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> getMyPoint = NetRetrofit.getInstance().getService().getMyPoint(objJson);
        getMyPoint.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    shopPoint.setText("내 사료 : " + result); // 불러와서 사료 textview에 새로갱신
                    compare = Integer.parseInt(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });

    }

    //광고 만들고 끝나면 재생성하는 코드
    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        // * 구매 완료시 호출
        // productId: 구매한 sku (ex) no_ads)
        // details: 결제 관련 정보
        if (productId.equals("purchased")) {
            // TODO: 구매 해 주셔서 감사합니다! 메세지 보내기
            Toast.makeText(getApplicationContext(), "구매 해 주셔서 감사합니다!", Toast.LENGTH_SHORT).show();
            bp.isPurchased("android.test.purchased");
            fpresenter.functionCount_update_rating(id, "1");
            //구입되면 레이팅을 1로 바꿔줌

            // * 광고 제거는 1번 구매하면 영구적으로 사용하는 것이므로 consume하지 않지만,
            // 만약 게임 아이템 100개를 주는 것이라면 아래 메소드를 실행시켜 다음번에도 구매할 수 있도록 소비처리를 해줘야한다.
            // bp.consumePurchase(Config.Sku);
        }else if(productId.equals("point100")){
            FunctionCountDTO dto = new FunctionCountDTO();
            dto.setUserid(id);
            fpresenter.functionCount_update_point(dto, 1); //100사료
            Toast.makeText(StoreActivity.this, "100사료 구입", Toast.LENGTH_SHORT).show();
            getMyPoint(id); // 구입하고 나면
            bp.consumePurchase("point100");
        }else if(productId.equals("point200")){
            FunctionCountDTO dto = new FunctionCountDTO();
            dto.setUserid(id);
            fpresenter.functionCount_update_point(dto, 2); //200사료
            Toast.makeText(StoreActivity.this, "200사료 구입", Toast.LENGTH_SHORT).show();
            getMyPoint(id); // 구입하고 나면 재호출
            bp.consumePurchase("point200");
        }
        else if(productId.equals("point300")){
            FunctionCountDTO dto = new FunctionCountDTO();
            dto.setUserid(id);
            fpresenter.functionCount_update_point(dto, 3); //100사료
            Toast.makeText(StoreActivity.this, "300사료 구입", Toast.LENGTH_SHORT).show();
            getMyPoint(id); // 구입하고 나면 재호출
            bp.consumePurchase("point300");
        }else if(productId.equals("donation")){
            Toast.makeText(StoreActivity.this, "감사합니다. 더 좋은 서비스를 제공하겠습니다.", Toast.LENGTH_SHORT).show();
            bp.consumePurchase("donation");
        }

    }

    @Override
    public void onPurchaseHistoryRestored() {
        // * 구매 정보가 복원되었을때 호출
        bp.loadOwnedPurchasesFromGoogle();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        // * 구매 오류시 호출
        // errorCode == Constants.BILLING_RESPONSE_RESULT_USER_CANCELED 일때는
        // 사용자가 단순히 구매 창을 닫은것임으로 이것 제외하고 핸들링하기.
        if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Toast.makeText(getApplicationContext(), "구매 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBillingInitialized() {
        // * 처음에 초기화됬을때.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }


    //구매한 폰트인지 체크
    private void CheckPurchasedfont(String font1 , String font2) {
        FontDTO dto = new FontDTO();
        dto.setUserid(id);
        dto.setFont(font1);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> checkpurchasedfont = NetRetrofit.getInstance().getService().checkpurchasedfont(objJson);
        checkpurchasedfont.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        int resultfont = Integer.parseInt(response.body().string());
                        //TODO : 샘물글씨체만 체크해도 괜찮음. 왜냐? 어차피 셋트로 구매하기 때문에
                        if (resultfont == 0 && font1.equals("dossaemmul.ttf")) {
                            nanumfont = 70;
                            //복고 구매
                            AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.purchaseproduct, null);
                            builder.setView(view);

                            final TextView snack = (TextView) view.findViewById(R.id.Tx_snack);
                            final TextView Tx_use = (TextView) view.findViewById(R.id.Tx_use);
                            final TextView Tx_cancel = (TextView) view.findViewById(R.id.Tx_cancel);
                            final AlertDialog dialog = builder.create();
                            getMyPoint2(snack);

                            //구매
                            Tx_use.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (compare < nanumfont) {
                                        Toast.makeText(getApplicationContext(), "간식을 구매해 주세요", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Gson gson = new Gson();
                                        JsonObject object = new JsonObject();

                                        object.addProperty("userid", id);
                                        object.addProperty("fontname1", font1);
                                        object.addProperty("fontname2", font2);

                                        String objJson = gson.toJson(object);
                                        Call<ResponseBody> insert_font = NetRetrofit.getInstance().getService().insert_font(objJson);
                                        insert_font.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    String a = null;
                                                    try {
                                                        a = response.body().string();

                                                        FunctionCountDTO dto = new FunctionCountDTO();
                                                        dto.setUserid(id);
                                                        fpresenter.functionCount_update_point(dto, 4);

                                                        getMyPoint(id);
                                                        dialog.dismiss();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Log.d("###fail", t.getMessage());
                                            }
                                        });

                                    }
                                }
                            });

                            Tx_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        } else if (resultfont == 0 && font1.equals("godomaum.ttf")) {
                            nanumfont = 70;
                            //복고 구매
                            AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.purchaseproduct, null);
                            builder.setView(view);

                            final TextView snack = (TextView) view.findViewById(R.id.Tx_snack);
                            final TextView Tx_use = (TextView) view.findViewById(R.id.Tx_use);
                            final TextView Tx_cancel = (TextView) view.findViewById(R.id.Tx_cancel);
                            final AlertDialog dialog = builder.create();
                            getMyPoint2(snack);

                            //구매
                            Tx_use.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (compare < nanumfont) {
                                        Toast.makeText(getApplicationContext(), "간식을 구매해 주세요", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Gson gson = new Gson();
                                        JsonObject object = new JsonObject();

                                        object.addProperty("userid", id);
                                        object.addProperty("fontname1", font1);
                                        object.addProperty("fontname2", font2);

                                        String objJson = gson.toJson(object);
                                        Call<ResponseBody> insert_font = NetRetrofit.getInstance().getService().insert_font(objJson);
                                        insert_font.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    String a = null;
                                                    try {
                                                        a = response.body().string();

                                                        FunctionCountDTO dto = new FunctionCountDTO();
                                                        dto.setUserid(id);
                                                        fpresenter.functionCount_update_point(dto, 4);

                                                        getMyPoint(id);
                                                        dialog.dismiss();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Log.d("###fail", t.getMessage());
                                            }
                                        });

                                    }
                                }
                            });

                            Tx_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 구입한 폰트입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("구입한 폰트 체크 실패", t.getMessage());
            }
        });
    }
}