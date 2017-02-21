import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
    needs: ['application'],
    temperature:null,
    humidity:null,


    userRooms: Ember.computed('model.user', function() {
  		var rooms = this.get("model.user").get("rooms");
      rooms.forEach( function(room) {
        room.bulbOn = false;
        $.ajax({
            url: 'https://api.particle.io/v1/devices/' + room.id + '/state?access_token=04b90f278a1415636513f0f71fe9f89e92cdfcba',
            type: "GET",
            async: false,
            success: function (response) {
              if(response.result == "ON") {
                room.bulbOn = true;
              }
            }
        });
      });
      return rooms;
  	}),

    user: Ember.computed('model.user', function() {
      return this.get("model.user");
    }),

    sensorReading: function() {
      var self = this;
      var deviceId = self.get('user').get('house').slice()[0].id;
      $.ajax({
          url: 'https://api.particle.io/v1/devices/' + deviceId + '/tempC?access_token=04b90f278a1415636513f0f71fe9f89e92cdfcba',
          type: "GET",
          async: false,
          success: function (response) {
              self.set("temperature",response.result.toFixed(2))
          }
      });
      $.ajax({
          url: 'https://api.particle.io/v1/devices/' + deviceId + '/humidity?access_token=04b90f278a1415636513f0f71fe9f89e92cdfcba',
          type: "GET",
          async: false,
          success: function (response) {
              self.set("humidity",response.result.toFixed(2))
          }
      });
      var Sensor = {"temperature":this.get("temperature"),"humidity":this.get("humidity")};
      return Sensor;
    }.property('currentTimePulse'),

    currentTimeMetronome: function(interval) {
      interval = interval || (1000*60);
      Ember.run.later(this, function() {
        this.notifyPropertyChange('currentTimePulse');
        this.currentTimeMetronome();
      }, interval);
    }.on('init'),

    actions: {
      changeLightState: function(id) {
        this.get("userRooms").forEach( function(room) {
          if(room.id == id) {
            var args = "off";
            if(!room.get("bulbOn")) {
              args = "on";
            }
            $.ajax({
                url: 'https://api.particle.io/v1/devices/' + room.id + '/led?access_token=04b90f278a1415636513f0f71fe9f89e92cdfcba',
                type: "POST",
                data: '{"args":"' + args + '"}',
                async: false,
                success: function (response) {
                  if(response.return_value == 1) {
                    room.set("bulbOn",!room.get("bulbOn"));
                  }
                }
            });
          }
        });
      }
    }
});
