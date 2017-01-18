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

	user: Ember.computed('model.user', function() {
		return this.get("model.user");
	}),

	temperatures: Ember.computed('model.temperatures', function() {
		return this.get("model.temperatures");
	}),

	humidities: Ember.computed('model.humidities', function() {
		return this.get("model.humidities");
	}),

	analyticType: Ember.computed('model.analyticType', function() {
		return this.get("model.analyticType").charAt(0).toUpperCase() + this.get("model.analyticType").slice(1);;
	}),

	data: Ember.computed('model', function() {
		var temperatures = {
			labels: this.get("model.temperatures").mapBy('timestamp'),
			datasets: [{
				label: '',
				data: this.get("model.temperatures").mapBy("value")
			}]
		};
		var humidities = {
			labels: this.get("model.humidities").mapBy('timestamp'),
			datasets: [{
				label: '',
				data: this.get("model.humidities").mapBy("value")
			}]
		};
		var leds = {
			labels: this.get("model.leds").mapBy('timestamp'),
			datasets: [{
				label: '',
				data: this.get("model.leds").mapBy("value")
			}]
		};
		if(this.get("analyticType") == "Temperature") {return temperatures;}
		else if (this.get("analyticType") == "Humidity") {return humidities;}
		else if (this.get("analyticType") == "Led") {return leds;}
	})

});
