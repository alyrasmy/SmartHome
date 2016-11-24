import DS from "ember-data";

export default DS.Model.extend({
  name: DS.attr('string'),
  username: DS.attr('string'),
  password: DS.attr('string'),
  email: DS.attr('string'),
  isadmin: DS.attr('boolean'),
  house: DS.hasMany('room'),
  rooms: DS.hasMany('room')
});
