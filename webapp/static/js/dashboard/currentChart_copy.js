// function currentChart(data){
//
//     var times = ["60초전","50초전","40초전","30초전","20초전","10초전"];
//     var colors = ["#ffd2b8","#d3bcff","#99d0fa","#ff9c9c","#fdbbd8","#8be7ad","#ee9ef1","#fdf187","#abf4fc"];
//
//      // var servers = ["Local"];
//      // var server0 = [100,120,130,110,200,130,130,130];
//
//     //servername (node)
//    // var servers = ["Local","192.168.0.1","192.168.0.2","192.168.0.3","192.168.0.4","192.168.0.5","192.168.0.6","192.168.0.7","192.168.0.8","192.168.0.9"];
//    // //server items
//    // var server0 = [100,120,130,110,200,130,130,130];
//    // var server1 = [10,20,30,40,30,30,40,30];
//    // var server2 = [20,20,20,20,40,30,40,30];
//    // var server3 = [49,40,10,6,30,30,40,10];
//    // var server4 = [40,70,90,10,50,20,90,60];
//    // var server5 = [20,50,60,20,90,10,30,30];
//    // var server6 = [30,40,10,40,30,80,20,90];
//    // var server7 = [29,10,20,9,90,50,10,70];
//    // var server8 = [84,30,46,1,20,10,80,10];
//    // var server9 = [32,20,20,80,50,20,60,20];
//
//
//     //real
//     // {"server1":"192.168.0.1","item":[14,20,30]}
//
//      // var dataset=[{
//     	// 	 label: servers[0],
//      //         backgroundColor: colors[0],
//      //         data: server0,
//      //         borderColor: colors[0],
//      //         borderWidth: 2,
//      //         fill:false
//      // }];
//     var stepSize = getCurrentStepSize(data);
//     var dataset=[];
//     for(var i=0;i<data.length;i++){
//    	 dataset[i]={
//          label: data[i]["ip"],
//          backgroundColor: colors[i],
//          data: data[i]["count"],
//          borderColor: colors[i],
//          borderWidth: 2,
//          fill:false
//    	 }
//     }
//
//     var ctx2 = document.getElementById("curchart").getContext('2d');
//     var lineChart = new Chart(ctx2, {
//         type: 'line',
//         data: {
//             labels: times,
//             datasets: dataset
//            // 	[{
//            //         label: servers[0],
//            //         backgroundColor: colors[0],
//            //         data: server0,
//            //         borderColor: colors[0],
//            //         borderWidth: 2,
//            //         fill:false
//            //     },
//            //     {
//            //         label: servers[1],
//            //         backgroundColor: colors[1],
//            //         data: server1,
//            //         borderColor: colors[1],
//            //         borderWidth: 2,
//            //         fill:false
//            //     },
//            //     {
//            //         label: servers[2],
//            //         backgroundColor: colors[2],
//            //         data: server2,
//            //         borderColor: colors[2],
//            //         borderWidth: 2,
//            //         fill:false
//            //     },
//            //     {
//            //         label: servers[3],
//            //         backgroundColor: colors[3],
//            //         data: server3,
//            //         borderColor: colors[3],
//            //         borderWidth: 3,
//            //         fill:false
//            //     },
//            //     {
//            //         label: servers[4],
//            //         backgroundColor: colors[4],
//            //         data: server4,
//            //         borderColor: colors[4],
//            //         borderWidth: 3,
//            //         fill:false
//            //     },
//            //     {
//            //         label: servers[5],
//            //         backgroundColor: colors[5],
//            //         data: server5,
//            //         borderColor: colors[5],
//            //         borderWidth: 3,
//            //         fill:false
//            //     },
//            //     {
//            //         label: servers[6],
//            //         backgroundColor: colors[6],
//            //         data: server6,
//            //         borderColor: colors[6],
//            //         borderWidth: 3,
//            //         fill:false
//            //     },
//            //     {
//            //         label: servers[7],
//            //         backgroundColor: colors[7],
//            //         data: server7,
//            //         borderColor: colors[7],
//            //         borderWidth: 3,
//            //         fill:false
//            //     },
//            //     {
//            //         label: servers[8],
//            //         backgroundColor: colors[8],
//            //         data: server8,
//            //         borderColor: colors[8],
//            //         borderWidth: 3,
//            //         fill:false
//            //     },
//            //     {
//            //         label: servers[9],
//            //         backgroundColor: colors[9],
//            //         data: server9,
//            //         borderColor: colors[9],
//            //         borderWidth: 3,
//            //         fill:false
//            //     }
//            //
//            // ]
//         },
//         options: {
//             layout: {
//                 padding: {
//                     top: 30,
//                     bottom: 20,
//                     left: 50,
//                     right: 50
//                 }
//             },
//             title: {
//                 display: true,
//                 text: '실시간 악성 코드 감염 현황',
//                 fontSize: 16
//             },
//             scales: {
//                 xAxes: [{
//                     ticks: {
//                         min: 0
//                     },
//                     stacked: false,
//                     scaleLabel: {
//                         display: false,
//                         labelString: '',
//                         fontSize: 16
//                     }
//                 }],
//                 yAxes: [{
//                     ticks: {
//                         // stepSize: 1
//                         min: 0,
//                         beginAtZero: true,
//                         // precision: 0,
//                         stepSize: stepSize
//                     },
//                     stacked: false,
//                     scaleLabel: {
//                         display: false,
//                         labelString: '',
//                         fontSize: 16
//                     },
//                 }]
//             }}
//     });
// }
//
// function getCurrentStepSize(data){
//     var num = 0;
//     for(var i=0;i<data.length;i++){
//         for(var j=0;j<data[i]["count"].length;j++) {
//             if(num<data[i]["count"][j]){
//                 num=data[i]["count"][j];
//             }
//         }
//     }
//
//     if(num<6){
//         return 1;
//     }else{
//         return 0;
//     }
// }
// function refreshCurrent(){
//     getCurrentChart();
// }
// function getReloadCurrentChart(){
//     var result = [];
//     $.ajax({
//         url:"/dashboard/server/chart/reload",
//         method:"GET",
//         data:{},
//         async: false,
//         error: function(err_res){
//             console.log("err",err_res)
//         },
//         success:function(suc_res){
//             // console.log("suc",suc_res)
//             result=suc_res;
//         }
//     });
//     result.pop();
//     // console.log("result",result)
//     return result;
// }
//
// function setReload(data){
//
//     //count : [0,0,0,0,0,0,0]
//
//     var obj = {
//         ip : "127.0.0.1",
//         count : [0,0,0,0,0,0,0],
//         interval: 10
//     }
//
//     var interval = obj.interval;
//
//
//
//
//     // var interval = data.pop();
//     // currentChart(data)
//     // setInterval(function (){
//     //     var intervalData = getReloadCurrentChart();
//     //     for(var i=0;i<intervalData.length;i++){
//     //         for(var j=0;j<intervalData[i]["count"].length;j++) {
//     //             data[i]["count"].shift();
//     //             data[i]["count"].push(intervalData[i]["count"][j]);
//     //         }
//     //     }
//     //     console.log(data);
//     //     currentChart(data)
//     // },interval*1000)
// }
//
// function getCurrentChart(){
//     $.ajax({
//         url:"/dashboard/server/chart",
//         method:"GET",
//         data:{},
//         error: function(err_res){
//             console.log("err",err_res)
//         },
//         success:function(suc_res){
//             console.log("get Current suc",suc_res)
//
//             setReload(suc_res);
//         }
//     });
//
//
//
//
// getCurrentChart()