import Ember from 'ember';

export default Ember.Route.extend({
  model: function(param) {
    let self = this;
    return Ember.RSVP.hash({
        user: self.get('store').find('user', self.controllerFor("application").get("username")),
        temperatures: self.get('store').find('temperature'),
        humidities: self.get('store').find('humidity')
      });
  },
  beforeModel: function() {
    if (!this.controllerFor("application").get("session.isAuthenticated")) {
        this.transitionTo("/");
    }
  },
  setupController: function(controller, model) {
    model.user.get('rooms').forEach( function (room){
      room.ledStartDate = new Date();
      room.ledEndDate = new Date();
    });
    controller.set('model', model)
  }
});
