import Ember from 'ember';

export default Ember.Route.extend({
  model: function(param) {
    let self = this;
    return Ember.RSVP.hash({
        user: self.get('store').find('user', self.controllerFor("application").get("username"))
      });
  },
  beforeModel: function() {
    if (!this.controllerFor("application").get("session.isAuthenticated")) {
        this.transitionTo("/");
    }
  }
});
