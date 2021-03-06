function addData(chart,label, data,count,allData) {
    var ylength = chart.options.scales.yAxes.length
    for(var i=0;i<ylength;i++){
        chart.options.scales.yAxes[i].ticks.stepSize=getCurrentStepSize(allData);
    }
    for(var i=6-count;i<data.length;i++) {
        chart.data.datasets.forEach(function(dataset){
            if(dataset.label == label) {
                dataset.data.shift();
            }
        });

        chart.data.datasets.forEach(function(dataset){
            if(dataset.label == label) {
                dataset.data.push(data[i]);
            }
        });
    }
    // chart.update();
}

function removeData(chart) {

    // chart.update();
}


function getCurrentInfo(chartInfo){

        var result = {};

        $.ajax({
            url:"/dashboard/server/chart",
            method:"GET",
            cache:false,
            async:false,
            error:function(res_err){

            },
            success: function(res_suc){
                result = res_suc;
                if(chartInfo!=null){
                    var count = res_suc.pop()/10;
                    for(var i=0;i<res_suc.length;i++) {
                        addData(chartInfo, res_suc[i].ip, res_suc[i].count,count,res_suc);
                    }

                    chartInfo.update();
                }



            }
        });


    return result;
}

function getCurrentStepSize(data){
    var num = 0;
    for(var i=0;i<data.length;i++){
        for(var j=0;j<data[i]["count"].length;j++) {
            if(num<data[i]["count"][j]){
                num=data[i]["count"][j];
            }
        }
    }

    if(num<6){
        return 1;
    }else{
        return 0;
    }
}

function initCurrentChart(data){

    var times = ["60초전","50초전","40초전","30초전","20초전","10초전"];
    var colors = ["#ffd2b8","#d3bcff","#99d0fa","#ff9c9c","#fdbbd8","#8be7ad","#ee9ef1","#fdf187","#abf4fc"];

    var stepSize = getCurrentStepSize(data);
    var dataset=[];
    for(var i=0;i<data.length;i++){
        dataset[i]={
            label: data[i]["ip"],
            backgroundColor: colors[i],
            data: data[i]["count"],
            borderColor: colors[i],
            borderWidth: 2,
            fill:false
        }
    }

    var ctx2 = document.getElementById("curchart").getContext('2d');
    var lineChart = new Chart(ctx2, {
        type: 'line',
        data: {
            labels: times,
            datasets: dataset
        },
        options: {
            layout: {
                padding: {
                    top: 30,
                    bottom: 20,
                    left: 50,
                    right: 50
                }
            },
            title: {
                display: true,
                text: '실시간 검사 현황',
                fontSize: 16
            },
            scales: {
                xAxes: [{
                    ticks: {
                        min: 0
                    },
                    stacked: false,
                    scaleLabel: {
                        display: false,
                        labelString: '',
                        fontSize: 16
                    }
                }],
                yAxes: [{
                    ticks: {
                        // stepSize: 1
                        min: 0,
                        beginAtZero: true,
                        // precision: 0,
                        stepSize: stepSize
                    },
                    stacked: false,
                    scaleLabel: {
                        display: false,
                        labelString: '',
                        fontSize: 16
                    },
                }]
            }}
    });

    return lineChart;

}
var currentInterval = 0;
var chartInfo = {};
function refreshCurrentChart(){
    clearInterval(currentInterval);
    chartInfo.clear();
    currentChart();
}

function currentChart(){

    var initInfo = getCurrentInfo();
    var interval = initInfo.pop();
    var test = 0;
    interval = interval*1000;
    chartInfo = initCurrentChart(initInfo);


    currentInterval=setInterval(function(){
        getCurrentInfo(chartInfo);
    }, interval);


}

currentChart();