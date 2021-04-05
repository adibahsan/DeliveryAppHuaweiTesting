import {StatusBar} from 'expo-status-bar';
import React from 'react';
import {StyleSheet, Text, View, Button, ToastAndroid} from 'react-native';
import BackgroundTimer from 'react-native-background-timer';
import axios from "axios";
import ReactNativeForegroundService from "@supersami/rn-foreground-service";
import { setWsHeartbeat } from "ws-heartbeat/client";



export const axiosInstance = axios.create({
    baseURL: "https://dadelivery.appigo.co" + "/riders-api",
    responseType: "json",
});


const webSocketNotifications = () =>{
    ReactNativeForegroundService.remove_task("taskid")


    ReactNativeForegroundService.add_task(() => console.log("I am Being Webhooked"), {
        delay: 10000,
        onLoop: false,
        taskId: "WEBSOCKET_4255",
        onSuccess: res => wsSetup(),
        onError: (e) => console.log(`Error logging:`, e),
    });

    ReactNativeForegroundService.start({
        id: 444,
        title: 'WebSocket Service',
        message: 'you are online!',
    }).then(r => console.log("WebScoket Service",r))
}


ReactNativeForegroundService.add_task(() => console.log("I am Being Tested"), {
    delay: 10000,
    onLoop: true,
    taskId: "taskid",
    onSuccess: res => syncFunction(),
    onError: (e) => console.log(`Error logging:`, e),
});

ReactNativeForegroundService.remove_task()


const wsSetup = () =>{
    console.log("Setupping WebSocket");
    const ws = new WebSocket("wss://974c23b8a17a.ngrok.io/web-socket");

    console.log("WebSocket is WebSocket",ws);

    setWsHeartbeat(ws, '{"Andrew":"ping"}',{
        pingTimeout: 60000, // in 60 seconds, if no message accepted from server, close the connection.
        pingInterval: 10000, // every 25 seconds, send a ping message to the server.
    });
}

const syncFunction = () =>{
    axiosInstance.get("/sync", {
        Authorization: "Bearer <AuthToken>"
    },
    },
    ).then((response) => {
    console.log("Sync OK" , response.data);
    if (response.status === 200) {
        ToastAndroid.showWithGravity(
            "Connection OK",
            ToastAndroid.SHORT,
            ToastAndroid.BOTTOM
        )
    }
}, (error) => {
    console.log(error.toString());
});
}

const registerBackgroundTimer = () => {
    console.log("Setting Timer");
    syncFunction();
    BackgroundTimer.runBackgroundTimer(() => {
//code that will be called every 15 seconds
            console.log("Update in Background");
            syncFunction();
        },
        15000);
}

const stopTimer = () => {
    console.log("Stopping Timer Before")
    BackgroundTimer.stopBackgroundTimer();
    console.log("Stopping Timer")
}

console.log("AxiosInstance", axiosInstance.defaults.baseURL);

export default function App() {
    return (
        <View style={styles.container}>
            <Text>Open up App.js to start working on your app!</Text>

            <View style={styles.containerTwo}>
                <View style={styles.buttonContainer}>
                    <Button onPress={
                        registerBackgroundTimer
                    } title="Background Timer"/>
                </View>
                <View style={styles.buttonContainer}>
                    <Button color ="red" title="Off" onPress={
                        stopTimer
                    } />
                </View>
            </View>

            <View style={styles.containerTwo}>
                <View style={styles.buttonContainer}>
                    <Button onPress={ () =>{

                        console.log("Starting Foreground Service");
                        ReactNativeForegroundService.start({
                            id: 144,
                            title: 'Running Service',
                            message: 'you are online!',
                        }).then(r => console.log("Started Service",r))
                    }
                    } title="Headless Task"/>
                </View>
                <View  style={styles.buttonContainer}>
                    <Button color ="red" title="Off" onPress={
                        ()=>{
                            ReactNativeForegroundService.stop().then(
                                r => console.log("Stopping Service",r)
                            )
                        }

                    }/>
                </View>
            </View>

            <View style={styles.containerTwo}>
                <View style={styles.buttonContainer}>
                    <Button onPress={ () =>{

                        console.log("Starting Websocket Service");
                        webSocketNotifications();
                    }
                    } title="Websocket Task"/>
                </View>
                <View  style={styles.buttonContainer}>
                    <Button color ="red" title="Off" onPress={
                        ()=>{
                            ReactNativeForegroundService.stop().then(
                                r => console.log("Stopping Service",r)
                            )
                        }

                    }/>
                </View>
            </View>
            <StatusBar style="auto"/>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 5,
        backgroundColor: '#fff',
        alignItems: 'center',
        justifyContent: 'center',
    },
    buttonStyle: {
        flex: 1,
        padding: 5,
        margin: 5,
        marginTop: 20,
        marginBottom: 20,
    },
    horizontalStyle: {
        flex: 1,
        // flexDirection: "row",
    },

    containerTwo: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        marginTop: -20,
        marginBottom: -20
    },
    buttonContainer: {
        flex: 1,
        padding : 5
    },
    buttonContainerRed: {
        flex: 1,
        backgroundColor: "red"
    }
});
headers: {
