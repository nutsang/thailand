package jp.jaxa.iss.kibo.rpc.PENUTSOR;

import android.graphics.Bitmap;
import android.util.Log;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import java.util.List;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import org.opencv.core.Mat;
import org.opencv.objdetect.QRCodeDetector;

public class YourService extends KiboRpcService {
    private int imageID = 1;
    private String textQRCode;
    private List<Integer> activeTargets;
    private Integer [] fristclose ={2,1,5,6,4,3};
    private Integer [] target1close ={6,2,5,4,3};
    private Integer [] target2close ={5,6,1,4,3};
    private Integer [] target3close ={4,5,6,2,1};
    private Integer [] target4close ={3,5,6,2,1};
    private Integer [] target5close ={6,4,2,1,3};
    private Integer [] target6close ={1,5,2,4,3};
    @Override
    protected void runPlan1(){
        api.startMission();
        while (true){
            List<Long> timeRemaining = api.getTimeRemaining();
            if (timeRemaining.get(1) < 60000) {
                break;
            }else{
                activeTargets = api.getActiveTargets();
                for (int id : activeTargets) {
                    switch(id){
                        case 1:
                            targetPoint1();
                            break;
                        case 2:
                            targetPoint2();
                            break;
                        case 3:
                            targetPoint3();
                            break;
                        case 4:
                            targetPoint4();
                            break;
                        case 5:
                            targetPoint5();
                            break;
                        case 6:
                            targetPoint6();
                            break;
                    }
                }
            }
        }
        qrCodePoint();
        api.reportMissionCompletion(reportQRCode());
    }
    @Override
    protected void runPlan2(){
       // write your plan 2 here
    }

    @Override
    protected void runPlan3(){
        // write your plan 3 here
    }

    private void centerPoint(){
        moveTo(10.7955d-0.1d,-8.0635d,5.1305d+0.15d,0f, 0f,-0.707f, 0.707f, false);
        saveMatNavCam();
    }

    private void targetPoint1(){
        //y==-10.0d+0.003d 
        //optimize  y==-9.92284d
        moveTo(11.2746d-0.0651d-0.002d, -9.92284d, 5.3625d+0.1111d, 0f, 0f, -0.707f, 0.707f,false);
        destroyTarget(1);
        centerPoint();
    }

    private void targetPoint2(){
        moveTo(10.612d-0.1302d-0.028d, -9.085172d-0.0572d-0.054d, 4.32d+0.04d, 0.5f ,0.5f ,-0.5f ,0.5f,false);
        destroyTarget(2);
        centerPoint();
    }

    private void targetPoint3(){
        moveTo(10.71d+0.00413d, -7.7d-0.0572d-0.0107d, 4.32d+0.04d, 0f, 0.707f, 0f, 0.707f, false);
        destroyTarget(3);
        centerPoint();
    }

    private void targetPoint4(){
        moveTo(10.3d+0.04d,-6.7185d+0.0572d+0.049d,5.1804d+(0.1111d/2d)-0.028d,0f,0f,-1f,0f, false);
        destroyTarget(4);
        centerPoint();
    }

    private void targetPoint5(){
        moveTo(11.114d-0.1302d+0.065d,-7.9756d+0.0572d,5.57d-0.04d,-0.5f,-0.5f,-0.5f,0.5f, false);
        destroyTarget(5);
        centerPoint();
    }

    private void targetPoint6(){
        moveTo(11.55d-0.06d ,-8.9929d-0.0572d+0.006d ,4.7818d+0.1111d+0.05d,0f ,0f ,0f ,1f, false);
        destroyTarget(6);
        centerPoint();
    }

    private void qrCodePoint(){
        moveTo(11.381944d+0.1177d, -8.566172d-0.0422d, 4.33d+0.04d, 0.5f ,0.5f ,-0.5f ,0.5f, false);
        readQRCode();
        centerPoint();
    }

    private void destroyTarget(int targetId){
        api.laserControl(true);
        api.takeTargetSnapshot(targetId);
    }

    private void readQRCode(){
        api.flashlightControlFront(0.05f);
        api.flashlightControlFront(0.0f);
        Mat image = api.getMatNavCam();
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        setTextQRCode(qrCodeDetector.detectAndDecode(image));
    }

    private String reportQRCode(){
        switch (getTextQRCode()){
            case "JEM":return "STAY_AT_JEM";
            case "COLUMBUS":return "GO_TO_COLUMBUS";
            case "RACK1":return "CHECK_RACK_1";
            case "ASTROBEE":return "I_AM_HERE";
            case "INTBALL":return "LOOKING_FORWARD_TO_SEE_YOU";
            case "BLANK":return "NO_PROBLEM";
            default:return null;
        }
    }

    private void saveBitmapNavCam(){
        Bitmap image = api.getBitmapNavCam();
        String imageName = "BitmapNavCam-" + getImageID() + ".jpg";
        api.saveBitmapImage(image, imageName);
        setImageID(getImageID() + 1);
    }

    private void saveBitmapDockCam(){
        Bitmap image = api.getBitmapDockCam();
        String imageName = "BitmapDockCam-" + getImageID() + ".jpg";
        api.saveBitmapImage(image, imageName);
        setImageID(getImageID() + 1);
    }

    private void saveMatNavCam(){
        Mat image = api.getMatNavCam();
        String imageName = "MatNavCam-" + getImageID() + ".jpg";
        api.saveMatImage(image, imageName);
        setImageID(getImageID() + 1);
    }

    private void saveMatDockCam(){
        Mat image = api.getMatDockCam();
        String imageName = "MatDockCam-" + getImageID() + ".jpg";
        api.saveMatImage(image, imageName);
        setImageID(getImageID() + 1);
    }

    private void moveTo(double pointX, double pointY, double pointZ, float quaternionX, float quaternionY, float quaternionZ, float quaternionW, boolean printRobotPosition){
        Point goalPoint = new Point(pointX, pointY, pointZ);
        Quaternion orientation = new Quaternion(quaternionX, quaternionY, quaternionZ, quaternionW);
        api.moveTo(goalPoint, orientation, printRobotPosition);
    }

    private void relativeMoveTo(double pointX, double pointY, double pointZ, float quaternionX, float quaternionY, float quaternionZ, float quaternionW, boolean printRobotPosition){
        Point goalPoint = new Point(pointX, pointY, pointZ);
        Quaternion orientation = new Quaternion(quaternionX, quaternionY, quaternionZ, quaternionW);
        api.relativeMoveTo(goalPoint, orientation, printRobotPosition);
    }

    private void showMessage(String message){
        Log.i("ShowMessage", message);
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getTextQRCode() {
        return textQRCode;
    }

    public void setTextQRCode(String textQRCode) {
        this.textQRCode = textQRCode;
    }

    private Integer Who_is_Close(List<Integer> selectClose){
        int size = list.size();
        if(size>1){
            Integer min = 9;
            Integer pick = null;
            for(Integer d:list) {
               Integer now= selectClose.indexOf(d);
               if(now<=min){
                   min=now;
                   pick=d;
               }
            }
            return pick;
        }
        return list.get(0);
    }
    private void wayNextfromDock(Integer d){
        switch(d){
            case 1:
                moveTo(11.2746d, -8.7314d+0.01d, 5.2988d, 0f, 0f, -0.707f, 0.707f, false);
                targetPoint1();
                break;
            case 2:
                targetPoint2();
                break;
            case 3:
                
                break;
            case 4:
                targetPoint4();
                break;
            case 5:
                targetPoint5();
                break;
            case 6:
                targetPoint6();
                break;
            case 7:
                qrCodePoint();
                break;
            default:
                return;
        }
    }
    private void doLaserTargetSnapshot(int target_id,boolean takeImage){
        api.laserControl(true);
        if(takeImage==true){
            try {
                Thread.sleep(2000);
                SaveImage(Capture(), "tar" + target_id + "laser");
                Thread.sleep(1000);
            } catch (InterruptedException a) {
                a.printStackTrace();
            }
        }
        api.takeTargetSnapshot(target_id);
    }
}
    