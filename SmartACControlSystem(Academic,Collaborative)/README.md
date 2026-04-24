# LINE Bot Air Conditioner Scheduler

# Control and schedule your air conditioner temperature via LINE Bot, powered by Remo3 and Google Sheets.

# This is a team class project created by Team 3.

# Project Overview

# Users can schedule air conditioner temperature changes at custom times using a LINE Bot on their smartphone.The system automatically adjusts the AC temperature at the preset time via Remo³, with all schedules stored in Google Sheets.

# Example:

# 17:00 → 26°C

# 03:00 → 28°C

# Features

# Schedule AC temperature by sending time and temperature via LINE Bot

# Automatically apply settings at specified time

# View schedules in LINE chat history

# Reset all schedules with a command

# Target Users

# People who want remote AC control from outside home

# Users who care about sleep environment

# Anyone tired of daily manual adjustments

# System Architecture

# Required Modules

# LINE Bot message handler (receive input, send response)

# Google Sheets read/write program

# Schedule data aggregator

# Time checker (compare current time with scheduled time)

# Remo3 AC controller

# System Workflow

# Get current time every minute

# Compare with scheduled time in Google Sheets

# If matched, send temperature command to Remo³

# Apply temperature change

# LINE Bot Flow

# Receive time / temperature from user

# Store temporary data

# Confirm schedule with 確定

# Save to official schedule sheet

# Reset all data with リセット

# Demo

# LINE Bot interaction: https://drive.google.com/file/d/1m0L8Q1CRZANIiC8dah5CU9k6mXYqDSEd/view

# Operation video: https://drive.google.com/file/d/1jehzboLo2pPeRyHv2moMgICPrF7-jm52/view

# Team \& Roles (Team 3)

# This is a collaborative group project. Each member contributed as follows:

# Hayano

# Time management program

# AC temperature control program

# Overall operation test

# Yamakawa

# Google Sheets I/O program

# Google Sheets I/O test

# Kayagiri

# LINE Bot UI design

# Presentation creation

# ZHU  (My Role)

# LINE Bot reply program

# Message translation between LINE Bot and system

# LINE Bot reply test

# Future Improvements

# Support multiple users

# Add AC on/off control

# Support mode switching (cool / heat / dry)

# Better error handling

# License

# This project is for educational purposes as part of a class presentation.

