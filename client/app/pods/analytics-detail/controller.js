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
		return this.get("model.analyticType");
	}),
});
