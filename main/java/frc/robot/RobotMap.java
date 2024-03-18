package frc.robot;
//This class comntains all motor ports and stuff like that

public class RobotMap {
    // drive system CAN IDs
    public static final int frontRightID = 4;
    public static final int frontLeftID = 6;
    public static final int backRightID = 3;
    public static final int backLeftID = 5;

    // drive system CAN IDs
    public static final int lifterArmMotor = 7;

    //shooter IDs
    public static final int leftShooterID = 1;
    public static final int rightShooterID = 2;

    //intake motors
    public static final int bottomIntakeMotorId = 9;
    public static final int topIntakeMotorId = 8;

    // lifter motor, pneumatics stuff
    public static final int liftUpID = 12;
    public static final int liftDownID = 11;

    // operator joystick mapping
    public static final int x = 1;
    public static final int a = 2;
    public static final int b = 3;
    public static final int y = 4;

    public static final int lt = 7;
    public static final int rt = 8;
    public static final int back = 9;

    // normal joystick mapping
    public static final int trigger = 1;
    public static final int speedShiftButton = 2;


    // this is a place for constants
    public static final double fastSpeed = 1;
    public static final double slowSpeed = 0.40;
    public static final double intakeSpeed = 0.5;
    public static final double armShootSpeed = 1;
    public static final double shootSpeed = -1;
    public static final double shooterIntakeSpeed = 0.25;
}
