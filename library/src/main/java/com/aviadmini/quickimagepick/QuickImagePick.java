package com.aviadmini.quickimagepick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Easy to use image picker. Use one of {@code pickFrom...} methods to trigger image pick and
 * get result in {@code onActivityResult} method of the same Activity or Fragment.
 */
@SuppressWarnings("unused")
@SuppressLint("NewApi")
public class QuickImagePick {

    private static final String PREFS_REQUEST_TYPE    = "req_type";
    private static final String PREFS_LAST_CAMERA_URI = "last_cam_uri";
    private static final String PREFS_CAMERA_DIR      = "cam_dir";

    private static final int REQ_CAMERA    = 4001;
    private static final int REQ_GALLERY   = 4002;
    private static final int REQ_DOCUMENTS = 4003;
    private static final int REQ_MULTIPLE  = 4004;

    public static final String ERR_CAMERA_NULL_RESULT         = "Camera returned bad/null data";
    public static final String ERR_CAMERA_CANNOT_WRITE_OUTPUT = "App cannot write to specified camera output directory";
    public static final String ERR_GALLERY_NULL_RESULT        = "Gallery returned bad/null data";
    public static final String ERR_DOCS_NULL_RESULT           = "Documents returned bad/null data";

    private static final boolean API_19 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    private static final boolean API_23 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    // ==== CAMERA ==== //

    /**
     * Capture new image using one of camera apps
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static boolean pickFromCamera(@NonNull final Activity pActivity, final int pRequestType) {

        final File file = createImageFile(pActivity);
        if (file == null) {
            return false;
        }
        final Intent intent = prepareCameraIntent(pActivity, createImageUri(pActivity, file), pRequestType);
        if (intent == null) {
            return false;
        }

        pActivity.startActivityForResult(intent, REQ_CAMERA);

        return true;
    }

    /**
     * Capture new image using one of camera apps
     *
     * @param pActivity activity which gets the result after pick flow
     */
    public static boolean pickFromCamera(@NonNull final Activity pActivity) {
        return pickFromCamera(pActivity, 0);
    }

    /**
     * Capture new image using one of camera apps
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static boolean pickFromCamera(@NonNull final Fragment pFragment, final int pRequestType) {

        final Context context = pFragment.getContext();

        final File file = createImageFile(context);
        if (file == null) {
            return false;
        }
        final Intent intent = prepareCameraIntent(context, createImageUri(context, file), pRequestType);
        if (intent == null) {
            return false;
        }

        pFragment.startActivityForResult(intent, REQ_CAMERA);

        return true;
    }

    /**
     * Capture new image using one of camera apps
     *
     * @param pFragment support fragment which gets the result after pick flow
     */
    public static boolean pickFromCamera(@NonNull final Fragment pFragment) {
        return pickFromCamera(pFragment, 0);
    }

    /**
     * Capture new image using one of camera apps
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static boolean pickFromCamera(@NonNull final android.app.Fragment pFragment, final int pRequestType) {

        final Context context = API_23 ? pFragment.getContext() : pFragment.getActivity();

        final File file = createImageFile(context);
        if (file == null) {
            return false;
        }
        final Intent intent = prepareCameraIntent(context, createImageUri(context, file), pRequestType);
        if (intent == null) {
            return false;
        }

        pFragment.startActivityForResult(intent, REQ_CAMERA);

        return true;
    }

    /**
     * Capture new image using camera apps
     *
     * @param pFragment fragment which gets the result after pick flow
     */
    public static boolean pickFromCamera(@NonNull final android.app.Fragment pFragment) {
        return pickFromCamera(pFragment, 0);
    }

    @Nullable
    private static Intent prepareCameraIntent(@NonNull final Context pContext, @NonNull final Uri pOutputFileUri, final int pRequestType) {

        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putInt(PREFS_REQUEST_TYPE, pRequestType)
                         .putString(PREFS_LAST_CAMERA_URI, pOutputFileUri.toString())
                         .apply();

        final Intent result = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        result.putExtra(MediaStore.EXTRA_OUTPUT, pOutputFileUri);

        return result;
    }

    @Nullable
    private static File createImageFile(@NonNull final Context pContext) {

        final File dir = getCameraPicsDirectory(pContext);
        if (dir == null) {
            return null;
        }

        final String fileName = UUID.randomUUID()
                                    .toString();

        return new File(dir, fileName);
    }

    private static Uri createImageUri(@NonNull final Context pContext, @NonNull final File pFile) {
        return FileProvider.getUriForFile(pContext, pContext.getPackageName() + ".qip_file_provider", pFile);
    }

    // ==== GALLERY ==== //

    /**
     * Pick image from Gallery
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromGallery(@NonNull final Activity pActivity, final int pRequestType) {

        final Intent intent = prepareGalleryIntent(pActivity, pRequestType);

        pActivity.startActivityForResult(intent, REQ_GALLERY);

    }

    /**
     * Pick image from Gallery
     *
     * @param pActivity activity which gets the result after pick flow
     */
    public static void pickFromGallery(@NonNull final Activity pActivity) {
        pickFromGallery(pActivity, 0);
    }

    /**
     * Pick image from Gallery
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromGallery(@NonNull final Fragment pFragment, final int pRequestType) {

        final Intent intent = prepareGalleryIntent(pFragment.getContext(), pRequestType);

        pFragment.startActivityForResult(intent, REQ_GALLERY);

    }

    /**
     * Pick image from Gallery
     *
     * @param pFragment support fragment which gets the result after pick flow
     */
    public static void pickFromGallery(@NonNull final Fragment pFragment) {
        pickFromGallery(pFragment, 0);
    }

    /**
     * Pick image from Gallery
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromGallery(@NonNull final android.app.Fragment pFragment, final int pRequestType) {

        final Intent intent = prepareGalleryIntent(API_23 ? pFragment.getContext() : pFragment.getActivity(), pRequestType);

        pFragment.startActivityForResult(intent, REQ_GALLERY);

    }

    /**
     * Pick image from Gallery
     *
     * @param pFragment fragment which gets the result after pick flow
     */
    public static void pickFromGallery(@NonNull final android.app.Fragment pFragment) {
        pickFromGallery(pFragment, 0);
    }

    @NonNull
    private static Intent prepareGalleryIntent(@NonNull final Context pContext, final int pRequestType) {

        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putInt(PREFS_REQUEST_TYPE, pRequestType)
                         .apply();

        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    // ==== DOCUMENTS ==== //

    /**
     * Pick image using Documents app (or file manager on pre-KitKat)
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromDocuments(@NonNull final Activity pActivity, final int pRequestType) {

        final Intent intent = prepareDocumentsIntent(pActivity, pRequestType);

        pActivity.startActivityForResult(intent, REQ_DOCUMENTS);

    }

    /**
     * Pick image using Documents app (or file manager on pre-KitKat)
     *
     * @param pActivity activity which gets the result after pick flow
     */
    public static void pickFromDocuments(@NonNull final Activity pActivity) {
        pickFromDocuments(pActivity, 0);
    }

    /**
     * Pick image using Documents app (or file manager on pre-KitKat)
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromDocuments(@NonNull final Fragment pFragment, final int pRequestType) {

        final Intent intent = prepareDocumentsIntent(pFragment.getContext(), pRequestType);

        pFragment.startActivityForResult(intent, REQ_DOCUMENTS);

    }

    /**
     * Pick image using Documents app (or file manager on pre-KitKat)
     *
     * @param pFragment support fragment which gets the result after pick flow
     */
    public static void pickFromDocuments(@NonNull final Fragment pFragment) {
        pickFromDocuments(pFragment, 0);
    }

    /**
     * Pick image using Documents app (or file manager on pre-KitKat)
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromDocuments(@NonNull final android.app.Fragment pFragment, final int pRequestType) {

        final Intent intent = prepareDocumentsIntent(API_23 ? pFragment.getContext() : pFragment.getActivity(), pRequestType);

        pFragment.startActivityForResult(intent, REQ_DOCUMENTS);

    }

    /**
     * Pick image using Documents app (or file manager on pre-KitKat)
     *
     * @param pFragment fragment which gets the result after pick flow
     */
    public static void pickFromDocuments(@NonNull final android.app.Fragment pFragment) {
        pickFromDocuments(pFragment, 0);
    }

    @NonNull
    private static Intent prepareDocumentsIntent(@NonNull final Context pContext, final int pRequestType) {

        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putInt(PREFS_REQUEST_TYPE, pRequestType)
                         .apply();

        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        return intent;
    }

    // ==== MULTITYPE ==== //

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final Activity pActivity, final int pRequestType, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {

        final Intent intent = prepareMultipleSourcesIntent(pActivity, pRequestType, pTitle, pPickSources);
        if (intent == null) {
            return false;
        }

        pActivity.startActivityForResult(intent, REQ_MULTIPLE);

        return true;
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final Activity pActivity, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {
        return pickFromMultipleSources(pActivity, 0, pTitle, pPickSources);
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final Fragment pFragment, final int pRequestType, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {

        final Intent intent = prepareMultipleSourcesIntent(pFragment.getContext(), pRequestType, pTitle, pPickSources);
        if (intent == null) {
            return false;
        }

        pFragment.startActivityForResult(intent, REQ_MULTIPLE);

        return true;
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final Fragment pFragment, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {
        return pickFromMultipleSources(pFragment, 0, pTitle, pPickSources);
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final android.app.Fragment pFragment, final int pRequestType,
                                                  @Nullable final String pTitle, @NonNull final PickSource... pPickSources) {

        final Intent intent = prepareMultipleSourcesIntent(API_23 ? pFragment.getContext() : pFragment.getActivity(), pRequestType, pTitle,
                pPickSources);
        if (intent == null) {
            return false;
        }

        pFragment.startActivityForResult(intent, REQ_MULTIPLE);

        return true;
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pTitle       chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final android.app.Fragment pFragment, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {
        return pickFromMultipleSources(pFragment, 0, pTitle, pPickSources);
    }

    @Nullable
    private static Intent prepareMultipleSourcesIntent(@NonNull final Context pContext, final int pRequestType, @Nullable final String pTitle,
                                                       @NonNull final PickSource... pSources) {

        // no sources - no work
        if (pSources.length == 0) {
            return null;
        }

        boolean addCamera = false;
        boolean addGallery = false;
        boolean addDocuments = false;
        for (final PickSource source : pSources) {

            switch (source) {

                case CAMERA: {

                    addCamera = true;

                    break;
                }

                case GALLERY: {

                    addGallery = true;

                    break;
                }

                case DOCUMENTS: {

                    addDocuments = true;

                    break;
                }

            }

        }

        final PackageManager packageManager = pContext.getPackageManager();

        final ArrayList<Intent> resultIntents = new ArrayList<>();

        // gather camera intents
        if (addCamera) {

            final List<Intent> cameraIntents = new ArrayList<>();

            final File file = createImageFile(pContext);

            if (file != null) {

                final Uri outputFileUri = createImageUri(pContext, file);

                final Intent cameraIntent = prepareCameraIntent(pContext, outputFileUri, pRequestType);

                final List<ResolveInfo> camList = packageManager.queryIntentActivities(cameraIntent, 0);
                for (final ResolveInfo resolveInfo : camList) {

                    final String packageName = resolveInfo.activityInfo.packageName;

                    final Intent intent = new Intent(cameraIntent);
                    intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                    cameraIntents.add(intent);

                }

            }

            resultIntents.addAll(cameraIntents);

        }

        // gather gallery intents
        if (addGallery) {

            final List<Intent> galleryIntents = new ArrayList<>();

            final Intent galleryIntent = prepareGalleryIntent(pContext, pRequestType);

            final List<ResolveInfo> camList = packageManager.queryIntentActivities(galleryIntent, 0);
            for (final ResolveInfo resolveInfo : camList) {

                final String packageName = resolveInfo.activityInfo.packageName;

                final Intent intent = new Intent(galleryIntent);
                intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                intent.setPackage(packageName);

                galleryIntents.add(intent);

            }

            resultIntents.addAll(galleryIntents);

        }

        // add documents intent
        if (addDocuments) {
            resultIntents.add(prepareDocumentsIntent(pContext, pRequestType));
        }

        // no components are able to perform pick
        if (resultIntents.size() == 0) {
            return null;
        }

        // create chooser intent
        final Intent result = Intent.createChooser(new Intent(), pTitle);
        result.putExtra(Intent.EXTRA_INITIAL_INTENTS, resultIntents.toArray(new Parcelable[resultIntents.size()]));

        return result;
    }

    // ==== RESULT HANDLING ==== //

    /**
     * Use this in {@code onActivityResult(...)} of Activity or Fragment to retrieve image {@link Uri}
     *
     * @param pFragment    support fragment
     * @param pRequestCode request code from {@code onActivityResult(...)} parameter
     * @param pResultCode  result code from {@code onActivityResult(...)} parameter
     * @param pData        Intent from {@code onActivityResult(...)} parameter
     * @param pCallback    result callback
     * @return true if result was accepted by QuickImagePick
     */
    public static boolean handleActivityResult(@NonNull final Fragment pFragment, final int pRequestCode, final int pResultCode,
                                               @Nullable final Intent pData, @NonNull final Callback pCallback) {
        return handleActivityResult(pFragment.getContext(), pRequestCode, pResultCode, pData, pCallback);
    }


    /**
     * Use this in {@code onActivityResult(...)} of Activity or Fragment to retrieve image {@link Uri}
     *
     * @param pFragment    fragment
     * @param pRequestCode request code from {@code onActivityResult(...)} parameter
     * @param pResultCode  result code from {@code onActivityResult(...)} parameter
     * @param pData        Intent from {@code onActivityResult(...)} parameter
     * @param pCallback    result callback
     * @return true if result was accepted by QuickImagePick
     */
    public static boolean handleActivityResult(@NonNull final android.app.Fragment pFragment, final int pRequestCode, final int pResultCode,
                                               @Nullable final Intent pData, @NonNull final Callback pCallback) {
        return handleActivityResult(API_23 ? pFragment.getContext() : pFragment.getActivity(), pRequestCode, pResultCode, pData, pCallback);
    }

    /**
     * Use this in {@code onActivityResult(...)} of Activity or Fragment to retrieve image {@link Uri}
     * <br>
     * Note: for Fragments it's advised to use Fragment-specific overloads of this method
     *
     * @param pContext     app {@link Context}
     * @param pRequestCode request code from {@code onActivityResult(...)} parameter
     * @param pResultCode  result code from {@code onActivityResult(...)} parameter
     * @param pData        Intent from {@code onActivityResult(...)} parameter
     * @param pCallback    result callback
     * @return true if result was accepted by QuickImagePick
     * @see #handleActivityResult(Fragment, int, int, Intent, Callback)
     * @see #handleActivityResult(android.app.Fragment, int, int, Intent, Callback)
     */
    public static boolean handleActivityResult(@NonNull final Context pContext, final int pRequestCode, final int pResultCode,
                                               @Nullable final Intent pData, @NonNull final Callback pCallback) {

        if (pRequestCode != REQ_CAMERA && pRequestCode != REQ_GALLERY && pRequestCode != REQ_DOCUMENTS && pRequestCode != REQ_MULTIPLE) {
            return false;
        }

        final int requestType = PreferenceManager.getDefaultSharedPreferences(pContext)
                                                 .getInt(PREFS_REQUEST_TYPE, 0);

        if (pResultCode == Activity.RESULT_OK) {

            if (pRequestCode == REQ_DOCUMENTS) {
                handleResultFromDocuments(pContext, requestType, pCallback, pData);
            } else if (pRequestCode == REQ_GALLERY) {
                handleResultFromGallery(pContext, requestType, pCallback, pData);
            } else if (pRequestCode == REQ_CAMERA) {
                handleResultFromCamera(pContext, requestType, pCallback);
            } else if (pData == null || pData.getData() == null) {
                handleResultFromCamera(pContext, requestType, pCallback);
            } else {
                handleResultFromDocuments(pContext, requestType, pCallback, pData);
            }

        } else {

            if (pRequestCode == REQ_DOCUMENTS) {
                pCallback.onCancel(PickSource.DOCUMENTS, requestType);
            } else if (pRequestCode == REQ_GALLERY) {
                pCallback.onCancel(PickSource.GALLERY, requestType);
            } else if (pRequestCode == REQ_CAMERA) {

                pCallback.onCancel(PickSource.CAMERA, requestType);

                deleteLastCameraPic(pContext);

            } else {

                if (pData == null || pData.getData() == null) {

                    pCallback.onCancel(PickSource.CAMERA, requestType);

                    deleteLastCameraPic(pContext);

                } else if (DocumentsContract.isDocumentUri(pContext, pData.getData())) {
                    pCallback.onCancel(PickSource.DOCUMENTS, requestType);
                } else {
                    pCallback.onCancel(PickSource.GALLERY, requestType);
                }

            }

        }

        return true;
    }

    private static void handleResultFromCamera(@NonNull final Context pContext, final int pRequestType, @NonNull final Callback pCallback) {

        final File cameraPicsDir = getCameraPicsDirectory(pContext);
        if (cameraPicsDir == null || !cameraPicsDir.canWrite()) {

            pCallback.onError(PickSource.CAMERA, pRequestType, ERR_CAMERA_CANNOT_WRITE_OUTPUT);

            return;
        }

        final Uri pictureUri = getLastCameraUri(pContext);

        if (pictureUri == null) {
            pCallback.onError(PickSource.CAMERA, pRequestType, ERR_CAMERA_NULL_RESULT);
        } else {
            pCallback.onImagePicked(PickSource.GALLERY, pRequestType, pictureUri);
        }

    }

    private static void handleResultFromGallery(@NonNull final Context pContext, final int pRequestType, @NonNull final Callback pCallback,
                                                @Nullable final Intent pData) {

        final Uri pictureUri = pData == null ? null : pData.getData();

        if (pictureUri == null) {
            pCallback.onError(PickSource.GALLERY, pRequestType, ERR_GALLERY_NULL_RESULT);
        } else {
            pCallback.onImagePicked(PickSource.GALLERY, pRequestType, pictureUri);
        }

    }

    private static void handleResultFromDocuments(@NonNull final Context pContext, final int pRequestType, @NonNull final Callback pCallback,
                                                  @Nullable final Intent pData) {

        final Uri pictureUri = pData == null ? null : pData.getData();

        if (pictureUri == null) {
            pCallback.onError(PickSource.DOCUMENTS, pRequestType, ERR_DOCS_NULL_RESULT);
        } else {
            pCallback.onImagePicked(PickSource.DOCUMENTS, pRequestType, pictureUri);
        }

    }

    // ==== CAMERA DIR ====//

    /**
     * @param pContext app {@link Context}
     * @return directory where pictures taken by camera apps will be saved or null if an error occurs.
     * By default it's a pictures directory on external storage.
     */
    public static File getCameraPicsDirectory(@NonNull final Context pContext) {

        final String camDirPath = PreferenceManager.getDefaultSharedPreferences(pContext)
                                                   .getString(PREFS_CAMERA_DIR, null);

        final File dir = camDirPath == null ? pContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) : new File(camDirPath);

        if (dir != null) {
            //noinspection ConstantConditions,ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        return dir;
    }

    /**
     * Change directory where pictures taken by camera apps will be saved. By default it's a pictures directory on external storage.
     * <br>
     * <b>Important: you are responsible for deleting the files once you're done with them.</b>
     *
     * @param pContext app {@link Context}
     * @param pDirPath path where images from camera will be saved. Use null to set default
     */
    public static void setCameraPicsDirectory(@NonNull final Context pContext, @Nullable final String pDirPath) {

        if (pDirPath == null) {
            PreferenceManager.getDefaultSharedPreferences(pContext)
                             .edit()
                             .remove(PREFS_CAMERA_DIR)
                             .apply();
        } else {
            PreferenceManager.getDefaultSharedPreferences(pContext)
                             .edit()
                             .putString(PREFS_CAMERA_DIR, pDirPath)
                             .apply();
        }

    }

    // ==== LAST CAM PIC ==== //

    public static Uri getLastCameraUri(@NonNull final Context pContext) {

        final String uriString = PreferenceManager.getDefaultSharedPreferences(pContext)
                                                  .getString(PREFS_LAST_CAMERA_URI, null);

        return uriString == null ? null : Uri.parse(uriString);
    }

    public static void deleteLastCameraPic(@NonNull final Context pContext) {

        final Uri uri = getLastCameraUri(pContext);
        if (uri != null) {
            pContext.getContentResolver()
                    .delete(uri, null, null);
        }

    }

    // ==== //

    // hide constructor
    private QuickImagePick() {}

    // ==== //

    /**
     * Callback for {@code handleActivityResult(...)} methods
     */
    public interface Callback {

        /**
         * Triggered when an image {@link Uri} was successfully retrieved
         *
         * @param pPickSource  source from which image {@link Uri} was retrieved
         * @param pRequestType request type that was (optionally) set when starting pick flow
         * @param pImageUri    {@link Uri} of the image
         */
        void onImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final Uri pImageUri);

        /**
         * Triggered when an error occurred in process of image picking
         *
         * @param pPickSource  source from which image {@link Uri} was retrieved
         * @param pRequestType request type that was (optionally) set when starting pick flow
         * @param pErrorString error string describing the error. One of public {@code ERR_} constants in {@link QuickImagePick} class
         */
        void onError(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final String pErrorString);

        /**
         * Triggered when picking flow was cancelled (mostly by user)
         *
         * @param pPickSource  source from which image {@link Uri} was retrieved.
         *                     Value might be inaccurate when multiple pick sources are requested and
         *                     user has not yet picked an exact one (from chooser).
         * @param pRequestType request type that was (optionally) set when starting pick flow
         */
        void onCancel(@NonNull final PickSource pPickSource, final int pRequestType);

    }

}
