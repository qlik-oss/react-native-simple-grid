export const getKey = (app, layout) => {
  return `${app.id}.${layout?.qInfo?.qId}.v2`;
};
