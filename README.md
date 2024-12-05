# Dice Jobs Bot

Bot used for easy apply on Dice.com jobs


## For Existing Users 🤓
To upgrade the program on your machine simply pull the main branch, in your terminal type
```shell
git pull
```

# For New Users ✨

## Clone the repo
```shell
git clone git@github.com:chunkingz/DiceJobsEasyApplyBot.git
```

## Navigate to the app directory
```shell
cd DiceJobsEasyApplyBot
```

## Add .env
```shell
touch .env
```

## Add your Dice credentials to the env file
```shell
EMAIL=your_email@example.com
PASSWORD=your_password
```

## Install the dependencies and build
```shell
mvn clean install
```

## Run the app
```shell
 mvn exec:java -Dexec.mainClass="com.chunkingz.Main"
```

## Extras
Edit the `config.json` to match your job search criteria

## Dev Config
> OpenJDK: v22
> 
> Maven: v3

<br>

Also feel free to submit issues and open PR's.
