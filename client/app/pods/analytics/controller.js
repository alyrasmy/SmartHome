import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
	needs: ['application'],

	user: Ember.computed('model.user', function() {
		return this.get("model.user");
	}),

	temperatures: Ember.computed('model.temperatures', function() {
		return this.get("model.temperatures");
	}),

	humidities: Ember.computed('model.humidities', function() {
		return this.get("model.humidities");
	}),

	houseId: Ember.computed('model.user', function() {
		return this.get("model.user").get("house").slice()[0].get("id");
	}),

	actions: {
		temperatureAnalytics: function() {
				this.transitionToRoute("analytics-detail","temperature");
		},
		humidityAnalytics: function() {
				this.transitionToRoute("analytics-detail","humidity");
		},
		ledAnalytics: function() {
				this.transitionToRoute("analytics-detail","led");
		}
	}
});
