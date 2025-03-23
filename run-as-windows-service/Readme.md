# Web Page Monitor Service Setup Guide

This guide will help you set up the Web Page Monitor Spring Boot application as a Windows service that starts automatically when your system boots up, even before users log in, and ensures it runs as long as internet connectivity is available.

## Prerequisites

- Windows 10 operating system
- Java Runtime Environment (JRE) installed and configured
- Web Page Monitor Spring Boot JAR file (`web-page-monitor-service-1.0.0.jar`)

## Installation Steps

### 1. Download Windows Service Wrapper (WinSW)

Download the latest version of WinSW from GitHub: https://github.com/winsw/winsw/releases

Choose the appropriate version for your system (usually WinSW-x64.exe for 64-bit systems).

### 2. Prepare Directory Structure

1. Create a directory at `C:\Tools` if it doesn't already exist
2. Place your Spring Boot JAR file (`web-page-monitor-service-1.0.0.jar`) in this directory
3. Create a `logs` subdirectory: `C:\Tools\logs`
4. Copy the downloaded WinSW executable to the `C:\Tools` directory
5. Rename the WinSW executable to `WebPageMonitorService.exe` for clarity

### 3. Create Configuration File

Create a file named `WebPageMonitorService.xml` in the same directory (`C:\Tools`) with the following content:

```xml
<service>
   <id>WebPageMonitorApp</id>
   <name>Web Page Monitor Spring App</name>
   <description>Spring Boot application running as a Windows service</description>
   <executable>javaw</executable>
   <arguments>-jar C:\Tools\web-page-monitor-service-1.0.0.jar -Dspring.mail.password="password"</arguments>
   <logpath>C:\Tools\logs</logpath>
   <log mode="roll-by-size">
     <sizeThreshold>10240</sizeThreshold>
     <keepFiles>8</keepFiles>
   </log>
   <startmode>Automatic</startmode>
   <depend>Tcpip</depend>
   <onfailure action="restart" delay="10 sec"/>
   <resetfailure>1 hour</resetfailure>
</service>
```

> **Note:** Replace the password in the `arguments` tag with your actual email service password.

### 4. Install the Windows Service

1. Open Command Prompt as Administrator
2. Navigate to the directory containing your files:
   ```
   cd C:\Tools
   ```
3. Install the service by running:
   ```
   WebPageMonitorService.exe install
   ```
4. Start the service:
   ```
   WebPageMonitorService.exe start
   ```

### 5. Verify Service Installation

1. Open the Windows Services manager:
   - Press `Win + R`, type `services.msc`, and press Enter
2. Look for "Web Page Monitor Spring App" in the list of services
3. Verify that its Status is "Running" and Startup Type is "Automatic"

## Service Management

- **Start the service:** `WebPageMonitorService.exe start`
- **Stop the service:** `WebPageMonitorService.exe stop`
- **Uninstall the service:** `WebPageMonitorService.exe uninstall`
- **Check service status:** `WebPageMonitorService.exe status`

## Configuration Details

- The service will automatically start when Windows boots
- It depends on TCP/IP services, ensuring internet connectivity is available
- The service will automatically restart after 10 seconds if it fails
- Logs are stored in `C:\Tools\logs` and will be rotated when they reach ~10MB

## Troubleshooting

If the service fails to start:

1. Check the logs in `C:\Tools\logs`
2. Verify that Java is properly installed and in your system PATH
3. Ensure the paths in the XML configuration file are correct
4. Check that the service has appropriate permissions to run