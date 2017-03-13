import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
	queryParams: ['startDate','endDate','roomId'],
	needs: ['application'],
	temperatureLoaded: true,
	humidityLoaded: true,
	startDate:"default",
	endDate:"default",
	roomId:"default",
	currentConditions:{"status":false},
	forecastConditionsStatus: false,
	summaryStats:{},
	homeSenorId: "230046001347343339383037",
	hasXAxisTitle: true,
	hasYAxisTitle: true,

	isLEDAnalytics:  Ember.computed('model', function() {
		if (this.get("analyticType") == "Led") {
			return true;
		}
		return false;
	}),

	chartAxises: Ember.computed('model', function() {
		var options = {
		  scales: {
		    yAxes: [{
		      scaleLabel: {
		        display: true,
		        labelString: 'probability'
		      }
		    }],
				yAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'probability'
					}
				}]
		  }
		}
		var chart1_xValueDisplayName = "";
		var chart1_yValueDisplayName = "";
		var chart2_xValueDisplayName = "";
		var chart2_yValueDisplayName = "";
		if(this.get("analyticType") == "Temperature") {
			chart1_xValueDisplayName = "Time(date)";
			chart1_yValueDisplayName = "Temperature(Celsius)";
			chart2_xValueDisplayName = "Temperature(Celsius)";
			chart2_yValueDisplayName = "Occurence(%)";
		} else if (this.get("analyticType") == "Humidity") {
			chart1_xValueDisplayName = "Time(date)";
			chart1_yValueDisplayName = "Humidity(%)";
			chart2_xValueDisplayName = "Humidity(%)";
			chart2_yValueDisplayName = "Occurence(%)";
		} else if (this.get("analyticType") == "Led") {
			chart1_xValueDisplayName = "Time(date)";
			chart1_yValueDisplayName = "LED usage(ms)";
			chart2_xValueDisplayName = "LED usage(ms)";
			chart2_yValueDisplayName = "Occurence(%)";
		}
		return { "chart1_xValueDisplayName": options,
						"chart1_yValueDisplayName": chart1_yValueDisplayName,
						"chart2_xValueDisplayName": chart2_xValueDisplayName,
						"chart2_yValueDisplayName": chart2_yValueDisplayName}
	}),

	convertUnixTimestamp: function (utc) {
			var date = new Date(utc*1000);
			var hours = date.getHours();
			var minutes = "0" + date.getMinutes();
			var seconds = "0" + date.getSeconds();

			return hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
	},

	forecastConditions: Ember.computed('model', function() {
			var	forecastConditions = [];
			return forecastConditions;
	}),

	user: Ember.computed('model.user', function() {
		return this.get("model.user");
	}),

	temperatures: Ember.computed('model.temperatures', function() {
		return this.get("model.temperatures");
	}),

	temperaturesByDate: Ember.computed('model.temperatures', function() {
		var self = this;
		var dates = [];
		this.get("model.temperatures").forEach( function (temperature){
			var dateOfReading = temperature.get('timestamp').toLocaleDateString();
			if(dates.indexOf(dateOfReading) < 0) {
				dates.push(dateOfReading);
			}
		})
		var temperaturesByDate = [];
		dates.forEach(function (date){
			var temperatures = self.get("model.temperatures").filter(function(temperature) {
				return temperature.get('timestamp').toLocaleDateString() == date
			});

			var low = Number.POSITIVE_INFINITY;
			var high = Number.NEGATIVE_INFINITY;
			var tmp;
			for (var i=temperatures.length-1; i>=0; i--) {
			    tmp = Number(temperatures[i].get('value'));
			    if (tmp < low) low = tmp;
			    if (tmp > high) high = tmp;
			}

			var average = temperatures.reduce(function(sum, temperature) {
				return sum + Number(temperature.get('value'));
			}, 0)/temperatures.length;
			temperaturesByDate.push({"date":date, "low":low, "average":average, "high":high})
		})
		return temperaturesByDate;
	}),

	temperaturesByOccurence: Ember.computed('model.temperatures', function() {
		var self = this;
		var values = [];
		this.get("model.temperatures").forEach( function (temperature){
			var temperatureRoundedUp = Number(Math.round(temperature.get('value')+'e1')+'e-1'); // 1 decimal place
			if(values.indexOf(temperatureRoundedUp) < 0) {
				values.push(temperatureRoundedUp);
			}
		})
		var temperaturesByOccurence = [];
		values.forEach(function (value){
			var temperatures = self.get("model.temperatures").filter(function(temperature) {
				return Number(Math.round(temperature.get('value')+'e1')+'e-1') == value
			});

			var occurence = (temperatures.reduce(function(sum, temperature) {
				return sum + 1;
			}, 0)/self.get("model.temperatures").slice().length) * 100;
			temperaturesByOccurence.push({"value":value, "occurence":occurence})
		})

		temperaturesByOccurence.sort(function(a, b) {
    return parseFloat(a.value) - parseFloat(b.value);
		});
		return temperaturesByOccurence;
	}),

	humidities: Ember.computed('model.humidities', function() {
		return this.get("model.humidities");
	}),

	humiditiesByDate: Ember.computed('model.humidities', function() {
		var self = this;
		var dates = [];
		this.get("model.humidities").forEach( function (humidity){
			var dateOfReading = humidity.get('timestamp').toLocaleDateString();
			if(dates.indexOf(dateOfReading) < 0) {
				dates.push(dateOfReading);
			}
		})
		var humiditiesByDate = [];
		dates.forEach(function (date){
			var humidities = self.get("model.humidities").filter(function(humidity) {
				return humidity.get('timestamp').toLocaleDateString() == date
			});

			var low = Number.POSITIVE_INFINITY;
			var high = Number.NEGATIVE_INFINITY;
			var tmp;
			for (var i=humidities.length-1; i>=0; i--) {
					tmp = Number(humidities[i].get('value'));
					if (tmp < low) low = tmp;
					if (tmp > high) high = tmp;
			}

			var average = humidities.reduce(function(sum, humidity) {
				return sum + Number(humidity.get('value'));
			}, 0)/humidities.length;
			humiditiesByDate.push({"date":date, "low":low, "average":average, "high":high})
		})
		return humiditiesByDate;
	}),

	humiditiesByOccurence: Ember.computed('model.humidities', function() {
		var self = this;
		var values = [];
		this.get("model.humidities").forEach( function (humidity){
			var humidityRoundedUp = Number(Math.round(humidity.get('value')+'e1')+'e-1'); // 1 decimal place
			if(values.indexOf(humidityRoundedUp) < 0) {
				values.push(humidityRoundedUp);
			}
		})
		var humiditiesByOccurence = [];
		values.forEach(function (value){
			var humidities = self.get("model.humidities").filter(function(humidity) {
				return Number(Math.round(humidity.get('value')+'e1')+'e-1') == value
			});

			var occurence = (humidities.reduce(function(sum, humidity) {
				return sum + 1;
			}, 0)/self.get("model.humidities").slice().length) * 100;
			humiditiesByOccurence.push({"value":value, "occurence":occurence})
		})

		humiditiesByOccurence.sort(function(a, b) {
		return parseFloat(a.value) - parseFloat(b.value);
		});
		return humiditiesByOccurence;
	}),

	leds: Ember.computed('model.leds', function() {
		return this.get("model.leds");
	}),

	ledsByDate: Ember.computed('model.leds', function() {
		var self = this;
		var dates = [];
		this.get("model.leds").forEach( function (led){
			var dateOfReading = led.get('timestamp').toLocaleDateString();
			if(dates.indexOf(dateOfReading) < 0) {
				dates.push(dateOfReading);
			}
		})
		var ledsByDate = [];
		dates.forEach(function (date){
			var leds = self.get("model.leds").filter(function(led) {
				return led.get('timestamp').toLocaleDateString() == date
			});

			var low = Number.POSITIVE_INFINITY;
			var high = Number.NEGATIVE_INFINITY;
			var tmp;
			for (var i=leds.length-1; i>=0; i--) {
					tmp = Number(leds[i].get('value'));
					if (tmp < low) low = tmp;
					if (tmp > high) high = tmp;
			}

			ledsByDate.push({"date":date, "usage":high})
		})
		return ledsByDate;
	}),

	ledsByOccurence: Ember.computed('model.leds', function() {
		var self = this;
		var values = [];
		this.get("model.leds").forEach( function (led){
			var ledRoundedUp = Number(Math.round(led.get('value')+'e1')+'e-1'); // 1 decimal place
			if(values.indexOf(ledRoundedUp) < 0) {
				values.push(ledRoundedUp);
			}
		})
		var ledsByOccurence = [];
		values.forEach(function (value){
			var leds = self.get("model.leds").filter(function(led) {
				return Number(Math.round(led.get('value')+'e1')+'e-1') == value
			});

			var occurence = (leds.reduce(function(sum, led) {
				return sum + 1;
			}, 0)/self.get("model.leds").slice().length) * 100 ;
			ledsByOccurence.push({"value":value, "occurence":occurence})
		})

		ledsByOccurence.sort(function(a, b) {
		return parseFloat(a.value) - parseFloat(b.value);
		});
		return ledsByOccurence;
	}),

	analyticType: Ember.computed('model.analyticType', function() {
		return this.get("model.analyticType").charAt(0).toUpperCase() + this.get("model.analyticType").slice(1);;
	}),

	data: Ember.computed('model', function() {
		var temperatures = {
			labels: this.get("temperaturesByDate").mapBy('date'),
			datasets: [{
				label: 'Low',
				fillColor: "#0957D6",
				data: this.get("temperaturesByDate").mapBy('low')
			},{
				label: 'Average',
				fillColor: "#E9EC35",
				data: this.get("temperaturesByDate").mapBy('average')
			},{
				label: 'High',
				fillColor: "#FF5733",
				data: this.get("temperaturesByDate").mapBy('high')
			}]
		};
		var temperaturesOccurence = {
			labels: this.get("temperaturesByOccurence").mapBy('value'),
			datasets: [{
				label: '',
				fillColor: "#232F43",
				data: this.get("temperaturesByOccurence").mapBy('occurence')
			}]
		};
		var humidities = {
			labels: this.get("humiditiesByDate").mapBy('date'),
			datasets: [{
				label: 'Low',
				fillColor: "#0957D6",
				data: this.get("humiditiesByDate").mapBy('low')
			},{
				label: 'Average',
				fillColor: "#E9EC35",
				data: this.get("humiditiesByDate").mapBy('average')
			},{
				label: 'High',
				fillColor: "#FF5733",
				data: this.get("humiditiesByDate").mapBy('high')
			}]
		};
		var humiditiesOccurence = {
			labels: this.get("humiditiesByOccurence").mapBy('value'),
			datasets: [{
				label: '',
				fillColor: "#232F43",
				data: this.get("humiditiesByOccurence").mapBy('occurence')
			}]
		};
		var leds = {
			labels: this.get("ledsByDate").mapBy('date'),
			datasets: [{
				label: '',
				fillColor: "#232F43",
				data: this.get("ledsByDate").mapBy('usage')
			}]
		};
		var ledsOccurence = {
			labels: this.get("ledsByOccurence").mapBy('value'),
			datasets: [{
				label: '',
				fillColor: "#232F43",
				data: this.get("ledsByOccurence").mapBy('occurence')
			}]
		};
		if(this.get("analyticType") == "Temperature") {return {"day":temperatures,"occurence":temperaturesOccurence};}
		else if (this.get("analyticType") == "Humidity") {return {"day":humidities,"occurence":humiditiesOccurence};}
		else if (this.get("analyticType") == "Led") {return {"day":leds,"occurence":ledsOccurence};}
	}),

	getCurrentConditions: Ember.computed('model', function() {
		var city = this.get('user').get('city');
		var self = this;
		$.ajax({
				url: 'http://api.openweathermap.org/data/2.5/weather?q=' + city +'&appid=1c0e8317c02db53fe393633793ca0f9b',
				type: "GET",
				data: { format: 'json' },
				dataType: 'jsonp',
				async: false,
				success: function (response) {
						var currentConditions = {};
						currentConditions.description = response.weather[0].description;
						currentConditions.icon = 'assets/' + response.weather[0].icon + '.png';
						currentConditions.temperature = (response.main.temp - 273).toFixed(2);
						currentConditions.pressure = response.main.pressure;
						currentConditions.humidity = response.main.humidity;
						currentConditions.temp_min = (response.main.temp_min - 273).toFixed(2);
						currentConditions.temp_max = (response.main.temp_max - 273).toFixed(2);
						currentConditions.sea_level = response.main.sea_level;
						currentConditions.grnd_level = response.main.grnd_level;
						currentConditions.windSpeed = response.wind.speed;
						currentConditions.windDirection = response.wind.deg;
						currentConditions.sunrise = self.convertUnixTimestamp(response.sys.sunrise);
						currentConditions.sunset = self.convertUnixTimestamp(response.sys.sunset);
						currentConditions.status = true;
						self.set("currentConditions",currentConditions)
				}
		});
	}),

	getSummaryStats: Ember.computed('model', function() {
		var self = this;
		if(this.get("analyticType") == "Temperature") {
			var analyticReadings = self.get("model.temperatures").slice();
			var deviceId = self.get('user').get('house').slice()[0].id;
			var analytic = "tempC";
		}	else if (this.get("analyticType") == "Humidity") {
			var analyticReadings = self.get("model.humidities").slice();
			var deviceId = self.get('user').get('house').slice()[0].id;
			var analytic = "humidity";
		}else if (this.get("analyticType") == "Led") {
			var analyticReadings = self.get("model.leds").slice();
			var deviceId = self.get("model.ledBoardId");
			var analytic = "usageTime";
		}

		var low = Number.POSITIVE_INFINITY;
		var high = Number.NEGATIVE_INFINITY;
		var tmp;
		for (var i=analyticReadings.length-1; i>=0; i--) {
				tmp = Number(analyticReadings[i].get('value'));
				if (tmp < low) low = tmp;
				if (tmp > high) high = tmp;
		}

		var average = analyticReadings.reduce(function(sum, reading) {
			return sum + Number(reading.get('value'));
		}, 0)/analyticReadings.length;

		var totalTimeUsage = analyticReadings.reduce(function(sum, reading) {
			return sum + Number(reading.get('value'));
		}, 0);

		var totalPowerUsage = ((totalTimeUsage/(60*60)) * 120) /1000; //120 mW/h

		$.ajax({
				url: 'https://api.particle.io/v1/devices/' + deviceId + '/' + analytic + '?access_token=04b90f278a1415636513f0f71fe9f89e92cdfcba',
				type: "GET",
				async: false,
				success: function (response) {
						var summaryStats = {};
						summaryStats.current = response.result.toFixed(2);
						summaryStats.avg = average.toFixed(2);
						summaryStats.max = high.toFixed(2);
						summaryStats.min = low.toFixed(2);
						summaryStats.totalPowerUsage = totalPowerUsage + " mW";
						summaryStats.price = "$0.11(mWh)";
						summaryStats.totalCost = "$" + (totalPowerUsage * 0.11);
						self.set("summaryStats",summaryStats)
				}
		});
	}),

	getforecastConditions: Ember.computed('model', function() {
		var city = this.get('user').get('city');
		var self = this;
		var counter =0;
		$.ajax({
				url: 'http://api.openweathermap.org/data/2.5/forecast?q=' + city +'&appid=1c0e8317c02db53fe393633793ca0f9b',
				type: "GET",
				data: { format: 'json' },
				dataType: 'jsonp',
				async: false,
				success: function (response) {
					  (response.list).forEach( function(forecast) {
							counter++;
							var condition = {};
							condition.id = '#' + counter + '!';
							condition.date = forecast.dt_txt;
							condition.description = forecast.weather[0].description;
							condition.icon = 'assets/' + forecast.weather[0].icon + '.png';
							condition.temperature = (forecast.main.temp - 273).toFixed(2);
							condition.pressure = forecast.main.pressure;
							condition.humidity = forecast.main.humidity;
							condition.temp_min = (forecast.main.temp_min - 273).toFixed(2);
							condition.temp_max = (forecast.main.temp_max - 273).toFixed(2);
							condition.sea_level = forecast.main.sea_level;
							condition.grnd_level = forecast.main.grnd_level;
							condition.windSpeed = forecast.wind.speed;
							condition.windDirection = forecast.wind.deg;
							if(forecast.dt_txt.split(' ')[1] == "00:00:00") {
								var date = new Date(forecast.dt_txt.split(' ')[0]);
								condition.date = date.toDateString().split(' ')[0]
																+ ' ' + date.toDateString().split(' ')[1]
																+ ' ' + date.toDateString().split(' ')[2]
								 								+ " (Night)";
								self.get('forecastConditions').pushObject(condition);
							} else if (forecast.dt_txt.split(' ')[1] == "12:00:00") {
								var date = new Date(forecast.dt_txt.split(' ')[0]);
								condition.date = date.toDateString().split(' ')[0]
																+ ' ' + date.toDateString().split(' ')[1]
																+ ' ' + date.toDateString().split(' ')[2]
																+ " (Morning)";
								self.get('forecastConditions').pushObject(condition)
							}

						});
						counter =0;
						self.set('forecastConditionsStatus', true);
				}
		});
	}),

});
