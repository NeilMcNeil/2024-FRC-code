// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

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

  //Lifter arm motor thing
  CANSparkMax armLifter = new CANSparkMax(RobotMap.lifterArmMotor, MotorType.kBrushless);

  //shooters
  CANSparkMax leftShooter = new CANSparkMax(RobotMap.leftShooterID, MotorType.kBrushless);
  CANSparkMax rightShooter = new CANSparkMax(RobotMap.rightShooterID, MotorType.kBrushless);

  // compressor
  //private final Compressor m_compressor = new Compressor(PneumaticsModuleType.CTREPCM);


  //private final PWMSparkMax m_leftDrive = new PWMSparkMax(0);
  //private final PWMSparkMax m_rightDrive = new PWMSparkMax(1);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(frontLeft, frontRight);
  private final XboxController opperatorJoystick = new XboxController(1);
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
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    m_timer.restart();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      // Drive forwards half speed, make sure to turn input squaring off
      m_robotDrive.arcadeDrive(0.5, 0.0, false);
    } else {
      m_robotDrive.stopMotor(); // stop robot
    }
  }

  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    //make robot move
    m_robotDrive.arcadeDrive(-m_joystick.getY(), -m_joystick.getX());

    if(m_joystick.getRawButtonPressed(0)){
      System.out.println("button pressed");
      leftShooter.set(0.5);
      rightShooter.set(-0.5);
    }

    // Make arm rise
    if (opperatorJoystick.getXButtonPressed()){
      System.out.println("x buttom pressed");
      armLifter.set(0.1);
    } else if (opperatorJoystick.getYButtonPressed()){
      System.out.println("y buttom pressed");
      armLifter.set(-0.1);
    }
  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
