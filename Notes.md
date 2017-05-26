# Full Raspberry Pi Installation

## Software Needed
* NOOBS v2.4.0: https://www.raspberrypi.org/downloads/noobs/
* Java SE Development Kit 8u131 - Linux ARM 32 Hard Float ABI: http://www.oracle.com/technetwork/java/javase/downloads/index.html
* Maven v3.5.0: http://maven.apache.org/download.cgi (Also see: [Installing Maven on the Raspberry Pi](https://www.xianic.net/post/installing-maven-on-the-raspberry-pi/))
* Docker-Compose v1.13.0: https://github.com/javabean/arm-compose/releases (Also see: [Getting docker-compose on Raspberry Pi (ARM) the easy way](https://www.berthon.eu/2017/getting-docker-compose-on-raspberry-pi-arm-the-easy-way/))
* [JBoss Fuse on Karaf v6.3.0](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=46901)

## Prepare SD Card
* Put SD Card into reader on laptop (assuming Fedora).
* Use Fedora Disks to reformat/repartition the SD Card as 1 FAT partition.
* Unzip `NOOBS_v2_4_0.zip` and copy to the SD Card's root directory.

## Setup Pi
* Put SD Card into Pi.
* Power up Pi.
* Install Raspbian.
---
* Change config values to your locale/desires:
```sh
$ sudo raspi-config
```
* Change password: `<password>`
* Change locale: `en_US UTF-8`
* Change Timezone: `US Eastern`
* Change Wi-fi Country: `US`
* Enable Interfaces: `SSH` and `VNC`
---
* Log into WiFi network to establish password/connection.
---
* Add the following to the end of `dhcpcd.conf` to get a static IP Address.
```sh
$ sudo vi /etc/dhcpcd.conf
```
Add:
```

# define static profile eth0
profile static_eth0
static ip_address=10.42.0.3/24
static routers=10.42.0.1
static domain_name_servers=10.42.0.1

# fallback to static profile on eth0
interface eth0
fallback static_eth0

# define static wlan0
interface wlan0
static ip_address=10.42.0.2/24
static routers=10.42.0.1
static domain_name_servers=10.42.0.1
```
---
```sh
$ sudo reboot
```
* Now good to go headless via SSH.

### Continue Pi Setup (Headless)
* On laptop: (may need to remove 10.42.0.2 from known hosts)
```sh
$ sudo vi ~/.ssh/known_hosts
```
* Continue on laptop:
```sh
$ ssh-copy-id -i ~/.ssh/id_rsa pi@10.42.0.2
$ ssh pi@10.42.0.2
```
---
* Edit sudoers to remove password requirement (at your discretion)
```sh
$ sudo visudo (to edit sudoers)
```
Change:
```
%sudo   ALL=(ALL:ALL) ALL
```
To:
```
%sudo   ALL=(ALL:ALL) NOPASSWD: ALL
```
---
* Update Pi:
```sh
$ sudo apt-get update
$ sudo apt-get dist-upgrade
$ sudo apt-get autoremove
$ sudo apt-get autoclean
$ sudo rpi-update
$ sudo reboot
```

### Install VNC Server:
```sh
$ sudo apt-get install tightvncserver
```
* Create Password:
```sh
$ vncserver :1
$ vncserver -kill :1
```
---
* Get VNCServer to start on boot:
```sh
$ sudo vi ~/.config/autostart/tightvnc.desktop
```
Create with:
```
[Desktop Entry]
Type=Application
Name=TightVNC
Exec=vncserver :1 -geometry 1280x800 -depth 24
StartupNotify=false
```
---
* Fix Cursor:
```sh
$ sudo vi ~/.vnc/xstartup
```
Change:
```
xsetroot -solid grey
```
To:
```
xsetroot -solid grey -cursor_name left_ptr
```
---
```sh
$ sudo reboot
```
* Now good to go headless via VNC.

## Configure Swap File

### Turn Off Swap
```sh
$ sudo dphys-swapfile swapoff
```

### Change Size (Required)
```sh
$ sudo vi /etc/dphys-swapfile
```
Change:
```
CONF_SWAPSIZE=100
```
To:
```
CONF_SWAPSIZE=512
```
```sh
$ sudo dphys-swapfile setup
```

### Change Location to USB (Optional)
* To save SD Card from corruption:
```sh
$ sudo vi /etc/dphys-swapfile
```
Change:
```
CONF_SWAPFILE=/var/swap
```
To:
```
CONF_SWAPFILE=/media/pi/<USB drive partition name>/swap
```
```sh
$ sudo dphys-swapfile setup
```
* If you do this change, you must have the thumbdrive plugged in before powering up the Pi.

### Turn Swap Back On
```sh
$ sudo dphys-swapfile swapon
```

## Install Java
* Copy jdk tarball to Pi.
```sh
$ sudo tar -zxvf jdk-8u131-linux-arm32-vfp-hflt.tar.gz -C /opt
$ sudo update-alternatives --install /usr/bin/java java /opt/jdk1.8.0_131/bin/java 1
$ sudo update-alternatives --install /usr/bin/javac javac /opt/jdk1.8.0_131/bin/javac 1
$ sudo update-alternatives --set java /opt/jdk1.8.0_131/bin/java
$ sudo update-alternatives --set javac /opt/jdk1.8.0_131/bin/javac
```
* Delete tarball

## Install Maven
* Copy maven tarball to Pi.
```sh
$ sudo tar -zxvf apache-maven-3.5.0-bin.tar.gz -C /opt
$ sudo vi /etc/profile.d/maven.sh
```
Create with:
```
export M2_HOME=/opt/apache-maven-3.5.0
export PATH=$PATH:$M2_HOME/bin
```
* Delete tarball

## Install Docker
```sh
$ curl -sSL https://get.docker.com/ | sh
$ sudo usermod -aG docker pi
$ sudo systemctl enable docker
```

## Install Docker-Compose:
* Copy docker-compose-Linux-armv7l to Pi.
```sh
$ sudo cp docker-compose-Linux-armv7l /usr/local/bin/docker-compose
$ sudo chown root:root /usr/local/bin/docker-compose
$ sudo chmod 0755 /usr/local/bin/docker-compose
```

## Cleanup and Verify
```sh
$ sudo reboot
```
```sh
$ java -version
$ git --version
$ mvn --version
$ docker version
$ docker-compse version
```

## Get Code
```sh
$ git clone http://github.com/MichaelFitzurka/Iot_Demo_Gateway.git
```
* Copy `apache-maven-3.5.0-bin.tar.gz` to `~/IoT_Demo_Gateway/Base/Docker_Files/software`.
* Copy `jdk-8u131-linux-arm32-vfp-hflt.tar.gz` to `~/IoT_Demo_Gateway/Base/Docker_Files/software`.
---
* Copy `jboss-fuse-karaf-6.3.0.redhat-187.zip` to `~/IoT_Demo_Gateway/Fuse/Docker_Files/software`.

## Build
```sh
$ cd ~/IoT_Demo_Gateway/Smart_Gateway
$ mvn clean install

$ cd ~/IoT_Demo_Gateway/Rules_CEP
$ mvn clean install

$ cd ~/IoT_Demo_Gateway/Base
$ docker build --rm -t psteiner/base .

$ cd ~/IoT_Demo_Gateway/Fuse
$ docker build --rm -t psteiner/fuse .

$ cd ~/IoT_Demo_Gateway
$ docker-compose build
```

# To Run
```sh
$ cd ~/IoT_Demo_Gateway
$ docker-compose up -d
$ docker-compose logs -f
```

# To Shutdown
```sh
$ docker-compose down
$ docker system prune
```
