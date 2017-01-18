import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
    needs: ['application'],
    userRooms: Ember.computed('model.user', function() {
  		var rooms = this.get("model.user").get("rooms");
      rooms.forEach( function(room) {
        room.bulbOn = false;
      });
      return rooms;
  	}),
    temp:10.3,
    hum:40.5,
    sensorReading: function() {
      this.set("hum",this.get("hum")+1);
      this.set("temp",this.get("temp")+1);
      var Sensor = {"temperature":this.get("temp"),"humidity":this.get("hum")};
      return Sensor;
    }.property('currentTimePulse'),

    currentTimeMetronome: function(interval) {
      interval = interval || 1000;
      Ember.run.later(this, function() {
        this.notifyPropertyChange('currentTimePulse');
        this.currentTimeMetronome();
      }, interval);
    }.on('init'),

    actions: {
      changeLightState: function(id) {
        this.get("userRooms").forEach( function(room) {
          if(room.id == id) {
            room.set("bulbOn",!room.get("bulbOn"));
          }
        });
      }
    }
});
