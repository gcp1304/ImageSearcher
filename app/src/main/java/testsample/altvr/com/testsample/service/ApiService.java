package testsample.altvr.com.testsample.service;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import testsample.altvr.com.testsample.RetrofitAdapter;
import testsample.altvr.com.testsample.PixabayRetrofitService;
import testsample.altvr.com.testsample.events.ApiErrorEvent;
import testsample.altvr.com.testsample.events.PhotosEvent;
import testsample.altvr.com.testsample.util.LogUtil;
import testsample.altvr.com.testsample.vo.PhotoResponseVo;

public class ApiService {
    private LogUtil log = new LogUtil(ApiService.class);
    private static String PIXABAY_API_KEY = "2387134-2e9952af7d840c1d7abc947b1";
    private static int MIN_IMAGE_WIDTH = 1000;
    private static int MIN_IMAGE_HEIGHT = 1000;
    private static String IMAGE_TYPE = "photo";

    private PixabayRetrofitService mService;
    private EventBus mEventBus;

    public ApiService(Context context) {
        log.d("Initializing ApiService");
        mService = RetrofitAdapter.getRestService(context);
        mEventBus = EventBus.getDefault();
    }

    /**
     * YOUR CODE HERE
     *
     * For part 1a, you should implement getDefaultPhotos and searchPhotos. These calls should make the proper
     * API calls to Pixabay and post PhotosEvents to the event bus for the fragments to fill themselves in.
     *
     * We provide a Retrofit API adapter here you can use, or you can roll your own using the HTTP library
     * of your choice.
     */
    public void getDefaultPhotos() {
        log.d("Invoke API");
        mService.getDefaultPhotos(PIXABAY_API_KEY, MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT, IMAGE_TYPE, new Callback<PhotoResponseVo>() {
            @Override
            public void success(PhotoResponseVo photoResponseVo, Response response) {
                log.d("Successfull reponse on API execute");
                processSuccessResponse(photoResponseVo);
            }

            @Override
            public void failure(RetrofitError error) {
                log.d("failure reponse on API execute");
                processFailureResponse(error);
            }
        });
    }

    public void searchPhotos(String searchString) {
        mService.searchPhotos(PIXABAY_API_KEY, searchString, MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT, IMAGE_TYPE, new Callback<PhotoResponseVo>() {
            @Override
            public void success(PhotoResponseVo photoResponseVo, Response response) {
                processSuccessResponse(photoResponseVo);
            }

            @Override
            public void failure(RetrofitError error) {
                processFailureResponse(error);
            }
        });
    }

    private void processSuccessResponse(PhotoResponseVo photoResponseVo) {
        log.d("Posting success message onto EventBus");
        if (!photoResponseVo.hits.isEmpty()) {
            mEventBus.post(new PhotosEvent(photoResponseVo.hits));
        } else {
            mEventBus.post(new PhotosEvent(null));
        }
    }

    private void processFailureResponse(RetrofitError retrofitError) {
        mEventBus.post(new ApiErrorEvent(retrofitError.getLocalizedMessage()));
    }
}
