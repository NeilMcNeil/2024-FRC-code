// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.XboxController;
//import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
//import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;



/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends TimedRobot {

  //driving motors
  CANSparkMax frontRight = new CANSparkMax(RobotMap.frontRightID, MotorType.kBrushless);
  CANSparkMax frontLeft = new CANSparkMax(RobotMap.frontLeftID, MotorType.kBrushless);
  CANSparkMax backRight = new CANSparkMax(RobotMap.backRightID, MotorType.kBrushless);
  CANSparkMax backLeft = new CANSparkMax(RobotMap.backLeftID, MotorType.kBrushless);

  // //Lifter arm motor thing
  CANSparkMax armLifter = new CANSparkMax(RobotMap.lifterArmMotor, MotorType.kBrushless);

  //intake motor
  PWMSparkMax bottomIntake = new PWMSparkMax(RobotMap.bottomIntakeMotorId);
  CANSparkMax topIntake = new CANSparkMax(RobotMap.topIntakeMotorId, MotorType.kBrushless);

  boolean isShooting = false;
  double speed = RobotMap.fastSpeed;

  //PneumaticsControlModule pcm = new PneumaticsControlModule(0);
  // Compressor compressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
  DoubleSolenoid leftSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 5);
  DoubleSolenoid rightSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 1, 6);

  //clicky thingy
  //DigitalInput limitSwitch = new DigitalInput(0);
  //limitSwitch.get()


  CANSparkMax leftShooter = new CANSparkMax(RobotMap.leftShooterID, MotorType.kBrushless);
  CANSparkMax rightShooter = new CANSparkMax(RobotMap.rightShooterID, MotorType.kBrushless);

  private final DifferentialDrive m_robotDrive = new DifferentialDrive(frontLeft, frontRight);
  private final GenericHID opperatorJoystick = new GenericHID(1);
  private final Joystick m_joystick = new Joystick(0);
  private final Timer m_timer = new Timer();

  public Robot() {
    SendableRegistry.addChild(m_robotDrive, frontLeft);
    SendableRegistry.addChild(m_robotDrive, frontRight);
  }

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    frontRight.setInverted(true);
    //make the motors follow each other
    backRight.follow(frontRight);
    backLeft.follow(frontLeft);

    armLifter.setIdleMode(IdleMode.kBrake);
    armLifter.getEncoder().setPosition(0);
    
    UsbCamera camera = CameraServer.startAutomaticCapture();
    camera.setResolution(4000, 4000);
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    m_timer.restart();
    leftSolenoid.set(DoubleSolenoid.Value.kOff);
    rightSolenoid.set(DoubleSolenoid.Value.kOff);
    // Alliance alliance = DriverStation.getAlliance();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    autoAmpshotAndSend(true, 0.15);
    //autoAmpshot(true);
    //shootInAuto();
    //forwardAuto();
    // else if (m_timer.get() < 2.5) {
    //   m_robotDrive.stopMotor();
    //   armLifter.set(-0.25);
    // }
  }

  public void disabledPeriodic() {
    SmartDashboard.putNumber("Arm Encoder", armLifter.getEncoder().getPosition());
  }
  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {
    leftShooter.stopMotor();
    rightShooter.stopMotor();
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    //make robot move
    m_robotDrive.arcadeDrive(-m_joystick.getY()*speed, -m_joystick.getX()*speed);

    // make arm move
    double armEncoderCount = armLifter.getEncoder().getPosition();
    if (opperatorJoystick.getRawButton(RobotMap.y)) {
      if (armEncoderCount < RobotMap.ARM_UP_ENCODER_COUNT) {
        armLifter.set(RobotMap.liftArmSpeed);
      }
      else {
        armLifter.set(0);
      }
    }
    else if (opperatorJoystick.getRawButton(RobotMap.a)) {
      if (armEncoderCount > RobotMap.ARM_DOWN_ENCODER_COUNT) {
        armLifter.set(-RobotMap.liftArmSpeed);
      }
      else {
        armLifter.set(0);
      }
    }
    else {
      armLifter.stopMotor();
    }


    // change speed if a button is pressed
    if(m_joystick.getRawButtonPressed(RobotMap.speedShiftButton)){
      if (speed == RobotMap.fastSpeed){
        speed = RobotMap.slowSpeed;
      }
      else if (speed == RobotMap.slowSpeed){
        speed = RobotMap.fastSpeed;
      }
    }

    // shoot
    if(m_joystick.getRawButtonPressed(RobotMap.trigger)){
      if (isShooting == true){
        leftShooter.stopMotor();
        rightShooter.stopMotor();
        isShooting = false;
      }
      else {
        leftShooter.set(RobotMap.shootSpeed);
        rightShooter.set(-RobotMap.shootSpeed);
        isShooting = true;
      }
    }
    else if(opperatorJoystick.getRawButton(RobotMap.back) && isShooting == false){
      leftShooter.set(RobotMap.shooterIntakeSpeed);
      rightShooter.set(-RobotMap.shooterIntakeSpeed);
    }
    else if (isShooting == false) {
      leftShooter.stopMotor();
      rightShooter.stopMotor();
    }


    // lift that lifter
    if(m_joystick.getRawButtonPressed(RobotMap.liftUpID)){
      leftSolenoid.set(DoubleSolenoid.Value.kForward);
      rightSolenoid.set(DoubleSolenoid.Value.kForward);
    }
    else if(m_joystick.getRawButtonPressed(RobotMap.liftDownID)){
      leftSolenoid.set(DoubleSolenoid.Value.kReverse);
      rightSolenoid.set(DoubleSolenoid.Value.kReverse);
    }
    
    // succ and shoot
    if (opperatorJoystick.getRawButton(RobotMap.rt)){
      bottomIntake.set(RobotMap.armShootSpeed);
      topIntake.set(-RobotMap.armShootSpeed);
    }
    else if (opperatorJoystick.getRawButton(RobotMap.lt)){
      bottomIntake.set(-RobotMap.intakeSpeed);
      topIntake.set(RobotMap.intakeSpeed);
    }
    else {
      bottomIntake.stopMotor();
      topIntake.stopMotor();
    }

    // nudge input
    // if (opperatorJoystick.getRawButton(RobotMap.rb)){
    //   bottomIntake.set(RobotMap.nudgeSpeed);
    //   topIntake.set(-RobotMap.nudgeSpeed);
    // }
    // else if (opperatorJoystick.getRawButton(RobotMap.lb)){
    //   bottomIntake.set(-RobotMap.nudgeSpeed);
    //   topIntake.set(RobotMap.nudgeSpeed);
    // }
    // else {
    //   bottomIntake.stopMotor();
    //   topIntake.stopMotor();
    // }
  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    testAuto();
  }

  public void autoAmpshotAndSend(boolean turnRight, double driveForwardTime) {
    float m = 1;
    if (turnRight != true){
      m = -1;
    }

    // turn
    if (m_timer.get() < driveForwardTime) {
      m_robotDrive.arcadeDrive(0.2, 0, false);
      leftShooter.set(RobotMap.shootSpeed);
      rightShooter.set(-RobotMap.shootSpeed);
    } 
    else if (m_timer.get() < 2.55 + driveForwardTime){
      shootInAuto(driveForwardTime);
    }
    else if (m_timer.get() < 2.8+driveForwardTime) {
      m_robotDrive.arcadeDrive(0, -0.5*m, false);
    }
    else if (m_timer.get() < 3.8+driveForwardTime) {
      // send it
      m_robotDrive.arcadeDrive(0.5, 0.0, false);
    }
    else{
      m_robotDrive.arcadeDrive(0, 0.0, false);
    }
  }

  public void forwardAuto() {
    if (m_timer.get() < 1.4) {
      // send it
      m_robotDrive.arcadeDrive(0.5, 0.0, false);
    } 
  }

  public void shootInAuto(double driveForwardTime){
    if (m_timer.get() < 2 + driveForwardTime){
      leftShooter.set(RobotMap.shootSpeed);
      rightShooter.set(-RobotMap.shootSpeed);
    }
    else if (m_timer.get() < 2.5 + driveForwardTime){
      bottomIntake.set(RobotMap.armShootSpeed);
      topIntake.set(-RobotMap.armShootSpeed);
    }
    else {
      leftShooter.stopMotor();
      rightShooter.stopMotor();
      bottomIntake.stopMotor();
      topIntake.stopMotor();
    }
  }

  public void testAuto(){
    if (m_timer.get() < 0.35) {
      m_robotDrive.arcadeDrive(0, -0.5, false);
    }
  }
}
