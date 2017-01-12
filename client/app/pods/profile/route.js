import Ember from 'ember';

export default Ember.Route.extend({
  username: function(controller, model) {
    return controller.get('username');
  },
  model: function(param) {
    let self = this;
    return Ember.RSVP.hash({
        user: self.get('store').find('user', self.controllerFor("application").get("username"))
      });
  },
});
