# Using UFW to setup a Firewall on Ubuntu 18.04

## What is UFW
UFW stands for Uncomplicated Firewall and is an interface to iptables that is geared towards simplifying the process of configuring a firewall. 



## Setting up a Basic Firewall
Ubuntu 18.04 severs can use the UFW firewall to make sure only connections to certain services are allowed. 

1. Install UFW if it isn't installed already
    - `sudo apt install ufw`
2. Start by settin default settings to ur UFW. The default settings basically state that we want to deny incoming but allow outgoing connections.
    - `sudo ufw default deny incoming`
    - `sudo ufw default allow outgoing`
3. Make sure that the firewall allows SSH connections
    - `ufw app list` -> Should show OpenSSH if you use it, and if it does, do the next step
    - `ufw allow OpenSSH`
4. Officially enable firewall in your server, but make sure that SSH is enabled so we can log back in!
    - `ufw enable`

5. Check the status of the firewall and it should show you all of the allowed connections
    - `ufw status`
6. Currently, the firewall is currently blocking all connections except for SSH, if you install and configure additional services, you will need to adjust the firewall settings to allow acceptable traffic in.
7. Allow HTTP-Port 80
    - `sudo ufw allow http` or `sudo ufw allow 80`
8. Allow HTTPS-Port 443
    - `sudo ufw allow https` or `sudo ufw allow 443`

## UFW Commands
**Checking the status of UFW**
- `sudo ufw status verbose`

**Allow Specific IP Addresses** <br>
You can also specify IP addresses. For example, if you want to allow connections from a specific IP Address sucha s a work or home IP address of 15.15.15.51
- `sudo ufw allow from 15.15.15.51`

You can also specify a specific port that the IP address is allowed to connect to
- `sudo ufw allow from 15.15.15.51 to any port 22`

If you want to allow a subnet of IP addresses. In this case, all of the IP addresses ranging from 15.15.15.1 to 15.15.15.254
- `sudo ufw allow from 15.15.15.0/24`

Or the Combined of the above
- `sudo ufw allow from 15.15.15.0/24 to any port 22`

**Allow connections to a Specific Network Interface**
1. First look up your network interfaces before continuing
   - `ip addr`
2. If your server has a public network interface called **eth0**, you could allow HTTP traffic to it with the following command
    - `sudo ufw allow in on eth0 to any port 80`

**Denying Rules**
Use any of the commands above with the allow -> deny

**Delete Rules by Rule Number**
1. Get the list of rules
    - `sudo ufw status numbered`
2. Delete the rule next to the rule number you want deleted
    - `sudo ufw delete 2`
## Advanced Settings for UFW that should be done
Please read down this section and setup the appropriate settings as necessary

**Allowing IPv6**
If your server has IPv6 enabled, ensure that UFW is configured to support IPv6 so that it will manage firewall rules for IPv6 in addition to IPv4.
- `sudo nano /etc/default/ufw`
- Ensure that IPV6 is equal to yes
  - IPV6=yes