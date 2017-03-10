import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
	needs: ['application'],

	temperatureStartDate: new Date(),
	temperatureEndDate: new Date(),
	humidityStartDate: new Date(),
	humidityEndDate: new Date(),
	temperatureStartDate: new Date(),
	temperatureEndDate: new Date(),

	hasMainRoomAnalyticsAccess:Ember.computed('model.user', function() {
		var houseId = this.get("model.user").get("house").slice()[0].get("id");
		if (houseId == "restricted") {
			return false;
		} else {
			return true;
		}
	}),

	rooms: Ember.computed('model.user', function() {
		return this.get("model.user").get("rooms").slice();
	}),

	temperatureStartDateString: Ember.computed('temperatureStartDate', function() {
		var date = this.get("temperatureStartDate");
		var month = date.getMonth() + 1;
		return date.getFullYear() + '/' + month + '/' + date.getDate()
	}),
	temperatureEndDateString: Ember.computed('temperatureEndDate', function() {
		var date = this.get("temperatureEndDate");
		var month = date.getMonth() + 1;
		return date.getFullYear() + '/' + month + '/' + date.getDate()
	}),
	humidityStartDateString: Ember.computed('humidityStartDate', function() {
		var date = this.get("humidityStartDate");
		var month = date.getMonth() + 1;
		return date.getFullYear() + '/' + month + '/' + date.getDate()
	}),
	humidityEndDateString: Ember.computed('humidityEndDate', function() {
		var date = this.get("humidityEndDate");
		var month = date.getMonth() + 1;
		return date.getFullYear() + '/' + month + '/' + date.getDate()
	}),
	ledDateChanged: Ember.computed('rooms.[]', function() {
		this.get("rooms").forEach( function (room){
			var date = room.ledStartDate;
			var month = date.getMonth() + 1;
			room.set('ledStartDateString', date.getFullYear() + '/' + month + '/' + date.getDate());
			date = room.ledEndDate;
			month = date.getMonth() + 1;
			room.set('ledEndDateString', date.getFullYear() + '/' + month + '/' + date.getDate());
		});
	}),

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
		},
		updateRoomDateString() {
			this.get("rooms").forEach( function (room){
				var date = room.ledStartDate;
				var month = date.getMonth() + 1;
				room.set('ledStartDateString', date.getFullYear() + '/' + month + '/' + date.getDate());
				date = room.ledEndDate;
				month = date.getMonth() + 1;
				room.set('ledEndDateString', date.getFullYear() + '/' + month + '/' + date.getDate());
			});
    }
	}
});
